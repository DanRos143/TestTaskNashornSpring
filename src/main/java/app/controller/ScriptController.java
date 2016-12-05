package app.controller;

import app.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import app.script.ScriptWrapper;
import app.service.ScriptService;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import javax.script.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping(value = "/api/scripts")
@ExposesResourceFor(ScriptWrapper.class)
public class ScriptController {
    private AtomicInteger counter = new AtomicInteger(0);
    private ScriptService service;

    @Autowired
    public void setService(ScriptService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/hal+json")
    public Resources<ScriptWrapper> getAll(){//temporary void
        //Collection<ScriptWrapper> scriptWrappers = service.getScriptWrappers();
        //Resources<ScriptWrapper> scriptWrapperResources = Resources.wrap()
        return null;
    }

    @GetMapping(value = "/{id}", produces = "application/hal+json")
    @JsonView(View.Rest.class)
    public ResponseEntity getScriptInfo(@PathVariable Integer id){
        ResponseEntity responseEntity;
        ScriptWrapper scriptWrapper = service.getScriptInfo(id);
        if (scriptWrapper == null) responseEntity = ResponseEntity.notFound().build();
        else {
            if (scriptWrapper.getLinks().isEmpty()){
                scriptWrapper.add(
                        linkTo(methodOn(ScriptController.class).getScriptInfo(id)).slash(id).withSelfRel(),
                        linkTo(methodOn(ScriptController.class).getScriptOutput(id)).slash(id + "/output").withRel("output"),
                        linkTo(methodOn(ScriptController.class).getScriptBody(id)).slash(id + "/body").withRel("body"),
                        linkTo(methodOn(ScriptController.class).killScript(id)).slash(id).withRel("delete")
                );
            }
            responseEntity = ResponseEntity.ok(scriptWrapper);
        }
        return responseEntity;
    }

    @PostMapping(value = "/async", consumes = "text/plain", produces = "text/plain")
    public ResponseBodyEmitter asyncScriptEval(@RequestBody String script,
                                          HttpServletResponse response)
            throws IOException, ExecutionException, InterruptedException {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(-1L);
        CompiledScript compiledScript = null;
        try {
             compiledScript = service.compile(script);
        } catch (ScriptException e) {
            emitter.completeWithError(e);
        }
        ScriptWrapper sW = new ScriptWrapper(counter.incrementAndGet(), script);
        service.saveResource(sW.getIdentifier(), sW);
        try {
            service.runAsynchronously(compiledScript, sW, emitter);
        } catch (ScriptException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    //it is working, but needs to be refactored later
    @PostMapping(value = "/sync", consumes = "text/plain", produces = "text/plain")
    public void syncScriptEval(@RequestBody String script,
                                          HttpServletResponse response) throws IOException, ScriptException {
        OutputStream out = response.getOutputStream();
        CompiledScript compiledScript = null;
        try {
            compiledScript = service.compile(script);
        } catch (ScriptException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        ScriptWrapper scriptWrapper = new ScriptWrapper(counter.incrementAndGet(), script);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setHeader("Location", "/api/scripts/" + scriptWrapper.getIdentifier());
        out.flush();
        service.saveResource(scriptWrapper.getIdentifier(), scriptWrapper);
        service.runSynchronously(compiledScript, scriptWrapper, out);
    }
    //big mistake, because there should be other links: rel=self should point on {id}/body and so on
    @GetMapping("/{id}/body")
    @JsonView(View.Body.class)
    public ResponseEntity getScriptBody(@PathVariable Integer id){
        return buildOkOrNotFound(id);
    }

    @GetMapping("/{id}/output")
    @JsonView(View.Output.class)
    public ResponseEntity getScriptOutput(@PathVariable Integer id){
        return buildOkOrNotFound(id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity killScript(@PathVariable Integer id){
        return service.stopScriptExecution(id);
    }

    @ExceptionHandler(ScriptException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleScripException(PrintWriter printWriter, ScriptException se){
        printWriter.println(se.getMessage());
        printWriter.flush();
        System.out.println(se.getMessage());
    }

    private ResponseEntity buildOkOrNotFound(Integer id){
        ResponseEntity responseEntity;
        ScriptWrapper scriptWrapper = service.getScriptInfo(id);
        if (scriptWrapper == null) responseEntity = ResponseEntity.notFound().build();
        else responseEntity = ResponseEntity.ok(scriptWrapper);
        return responseEntity;
    }

}