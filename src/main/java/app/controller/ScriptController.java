package app.controller;

import app.script.Script;
import app.script.ScriptResource;
import app.script.ScriptStatus;
import app.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.service.ScriptService;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import javax.script.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@RestController
@RequestMapping(value = "/api/scripts")
@ExposesResourceFor(Script.class)
public class ScriptController {
    private AtomicInteger counter = new AtomicInteger(0);
    private ResourceAssemblerSupport<Script, ScriptResource> assembler;
    private ScriptService service;

    @Autowired
    public void setService(ScriptService service) {
        this.service = service;
    }

    @Autowired
    public void setAssembler(ResourceAssemblerSupport assembler) {
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @JsonView(View.Rest.class)
    public Resources<ScriptResource> getAll() {
        return new Resources<>(
                assembler.toResources(service.getScripts()),
                linkTo(ScriptController.class).withSelfRel(),
                linkTo(ScriptController.class)
                        .slash("async")
                        .withRel("asyncExecution"),
                linkTo(ScriptController.class)
                        .slash("sync")
                        .withRel("syncExecution")
        );
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @JsonView(View.Rest.class)//hardcoded constants?
    public ResponseEntity getScript(@PathVariable Integer id){
        return createResponseEntity(id, "rest");
    }

    @GetMapping(value = "/{id}/body", produces = MediaTypes.HAL_JSON_VALUE)
    @JsonView(View.Body.class)
    public ResponseEntity getScriptBody(@PathVariable Integer id){
        return createResponseEntity(id, "body");
    }

    @GetMapping(value = "/{id}/output", produces = MediaTypes.HAL_JSON_VALUE)
    @JsonView(View.Output.class)
    public ResponseEntity getScriptOutput(@PathVariable Integer id){
        return createResponseEntity(id, "output");
    }

    @PostMapping(value = "/async", consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> asyncScriptEval(@RequestBody String body) throws ScriptException {
        CompiledScript compiledScript = service.compile(body);
        Script script = new Script(counter.incrementAndGet(), body);
        service.saveScript(script.getId(), script);
        service.runAsync(compiledScript, script);
        return ResponseEntity.created(UriComponentsBuilder
                .fromPath("/api/scripts/{id}")
                .buildAndExpand(script.getId())
                .toUri())
                .build();
    }

    @PostMapping(value = "/sync", consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<StreamingResponseBody> syncScriptEval(@RequestBody String body)// which type T should be? not sure about StreamingResponseBody
            throws ScriptException {
        CompiledScript compiledScript = service.compile(body);
        Script script = new Script(counter.incrementAndGet(), body);
        service.saveScript(script.getId(), script);
        try {//?
            service.runSync(compiledScript, script);
        } catch (IOException e) {
            script.stopScriptExecution();
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(script);

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity stopScriptExecution(@PathVariable Integer id) {
        return Optional.ofNullable(service.getScript(id))
                .map(script -> {
                    script.stopScriptExecution();
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(IllegalArgumentException::new);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ScriptException.class)
    public ResponseEntity handleScriptException(ScriptException se){
        return ResponseEntity.badRequest().body(se.getMessage());
    }

    private ResponseEntity createResponseEntity(Integer id, String type){
        return Optional.ofNullable(service.getScript(id))
                .map(script -> {
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
                                    .slash(id)
                                    .slash("body")
                                    .withSelfRel());
                            break;
                        case "output":
                            resource.getLinks().clear();
                            resource.add(linkTo(methodOn(ScriptController.class).getScriptOutput(id))
                                    .slash(id)
                                    .slash("output")
                                    .withSelfRel());
                            break;
                    }
                    return ResponseEntity.ok(resource);
                })
                .orElseThrow(IllegalArgumentException::new);
    }

}
        /*CompiledScript compiledScript = service.compile(body);
        Script script = new Script(counter.incrementAndGet(), body);
        service.saveScript(script.getId(), script);
        return ResponseEntity.created(UriComponentsBuilder
                .fromPath("/api/scripts/{id}")
                .buildAndExpand(script.getId())
                .toUri())
                .body(script);*/