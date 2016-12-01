package rest.controller;

import jdk.nashorn.internal.runtime.ParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import rest.dto.BodyDTO;
import rest.dto.OutPutDTO;
import rest.script.ScriptWrapper;
import rest.service.ScriptService;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.*;


@RestController
@RequestMapping(value = "/api/scripts/")
public class ScriptEvalController {
    private ScriptService service;

    @Autowired
    public void setService(ScriptService service) {
        this.service = service;
    }

    @GetMapping(value = "{id}", produces = "application/hal+json")
    public ResponseEntity getScriptInfo(@PathVariable Integer id){
        ResponseEntity responseEntity;
        ScriptWrapper scriptWrapper = service.getScriptInfo(id);
        if (scriptWrapper == null) responseEntity = ResponseEntity.notFound().build();
        else {
            if (scriptWrapper.getLinks().isEmpty()){
                scriptWrapper.add(Arrays.asList(
                        linkTo(methodOn(ScriptEvalController.class).getScriptInfo(id)).slash(id).withSelfRel(),
                        linkTo(methodOn(ScriptEvalController.class).getScriptOutput(id)).slash(id + "/output").withRel("output"),
                        linkTo(methodOn(ScriptEvalController.class).getScriptBody(id)).slash(id + "/body").withRel("body"),
                        linkTo(methodOn(ScriptEvalController.class).killScript(id)).slash(id).withRel("delete")
                        )
                );
            }
            responseEntity = ResponseEntity.ok(scriptWrapper);
        }
        return responseEntity;
    }

    @GetMapping
    public Set<String> getLinks(){
        return service.getLinks("/api/scripts/");//hardcoded, issue
    }

    @GetMapping("{id}/body")//???
    public BodyDTO getScriptBody(@PathVariable Integer id){
        return null;
    }

    @GetMapping("{id}/output")
    public OutPutDTO getScriptOutput(@PathVariable Integer id){
        return null;
    }

    @PostMapping(value = "async", consumes = "text/plain", produces = "text/plain")
    public ResponseBodyEmitter asyncScriptEval(@RequestBody String script,
                                          HttpServletResponse response)
            throws IOException, ExecutionException, InterruptedException {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        return null;
    }

    @PostMapping(value = "sync", consumes = "text/plain", produces = "text/plain")
    public void syncsScriptEval(@RequestBody String script,
                                          HttpServletResponse response)
            throws IOException, ExecutionException, InterruptedException {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            service.runSynchronously(script, writer);
        } catch (ScriptException e) {
            writer.println(e.getMessage());
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity killScript(@PathVariable Integer id){
        return service.stopScriptExecution(id);
    }

}
