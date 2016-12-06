package app.controller;

import app.script.Script;
import app.script.ScriptResource;
import app.script.ScriptResourceAssembler;
import app.script.ScriptStatus;
import app.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import app.service.ScriptService;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import javax.script.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping(value = "/api/scripts")
@ExposesResourceFor(Script.class)
public class ScriptController {
    private AtomicInteger counter = new AtomicInteger(0);
    private ScriptResourceAssembler assembler = new ScriptResourceAssembler();
    private ScriptService service;

    @Autowired
    public void setService(ScriptService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/hal+json")
    @JsonView(View.Rest.class)
    public Resources<ScriptResource> getAll(){
        return new Resources<>(
                assembler.toResources(service.getScripts()),
                linkTo(ScriptController.class).withSelfRel(),
                linkTo(ScriptController.class)
                        .slash("async").withRel("asyncExecution"),
                linkTo(ScriptController.class)
                        .slash("sync").withRel("syncExecution")
        );
    }

    @GetMapping(value = "/{id}", produces = "application/hal+json")
    @JsonView(View.Rest.class)
    public ResponseEntity getScript(@PathVariable Integer id){
        return createResponseEntity(id, "rest");
    }

    @GetMapping(value = "/{id}/body", produces = "application/hal+json")
    @JsonView(View.Body.class)
    public ResponseEntity getScriptBody(@PathVariable Integer id){
        return createResponseEntity(id, "body");
    }

    @GetMapping(value = "/{id}/output", produces = "application/hal+json")
    @JsonView(View.Output.class)
    public ResponseEntity getScriptOutput(@PathVariable Integer id){
        return createResponseEntity(id, "output");
    }

    @PostMapping(value = "/async", consumes = "text/plain", produces = "text/plain")
    public ResponseEntity<ResponseBodyEmitter> asyncScriptEval(@RequestBody String body)
            throws IOException {
        ResponseEntity<ResponseBodyEmitter> entity;
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(-1L);
        Script script;
        try {
            CompiledScript compiledScript = service.compile(body);
            script = new Script(counter.incrementAndGet(), body);
            service.saveScript(script.getId(), script);
            service.runAsynchronously(compiledScript, script, emitter);
            entity = ResponseEntity.created(UriComponentsBuilder
                    .fromPath("/api/scripts/{id}")
                    .buildAndExpand(script.getId())
                    .toUri()).body(emitter);
        } catch (ScriptException e) {
            entity = ResponseEntity.badRequest().body(emitter);
            emitter.send(e.getMessage() + "\n");
            emitter.complete();
        }
        return entity;
    }

    @PostMapping(value = "/sync", consumes = "text/plain", produces = "text/plain")
    public void syncScriptEval(@RequestBody String body, HttpServletResponse response)
            throws IOException {
        OutputStream out = response.getOutputStream();
        try {
            CompiledScript compiledScript = service.compile(body);
            Script script = new Script(counter.incrementAndGet(), body);
            service.saveScript(script.getId(), script);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setHeader("Location", "/api/scripts/" + script.getId());
            out.flush();
            service.runSynchronously(compiledScript, script, out);
        } catch (ScriptException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(e.getMessage().getBytes());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity stopScriptExecution(@PathVariable Integer id){
        boolean found = service.stopScriptExecution(id);
        if (found) return ResponseEntity.ok().build();
        else return ResponseEntity.notFound().build();
    }

    private ResponseEntity createResponseEntity(Integer id, String type){
        ResponseEntity entity;
        Script script = service.getScript(id);
        if (script == null) entity = ResponseEntity.notFound().build();
        else {
            ScriptResource resource = assembler.toResource(script);
            switch (type){
                case "rest":
                    resource.add(
                            linkTo(methodOn(ScriptController.class).getScriptOutput(id))
                                    .slash(id + "/output").withRel("output"),
                            linkTo(methodOn(ScriptController.class).getScriptBody(id))
                                    .slash(id + "/body").withRel("body")
                    );
                    if (!resource.getStatus().equals(ScriptStatus.Done) &&
                            !resource.getStatus().equals(ScriptStatus.Error))
                        resource.add(linkTo(methodOn(ScriptController.class).stopScriptExecution(id))
                                .slash(id).withRel("stop"));
                    break;
                case "body":
                    resource.getLinks().clear();
                    resource.add(linkTo(methodOn(ScriptController.class).getScriptBody(id))
                            .slash(id).withSelfRel());
                    break;
                case "output":
                    resource.getLinks().clear();
                    resource.add(linkTo(methodOn(ScriptController.class).getScriptOutput(id))
                            .slash(id + "/output").withSelfRel());
                    break;
            }
            entity = ResponseEntity.ok(resource);
        }
        return entity;
    }

}