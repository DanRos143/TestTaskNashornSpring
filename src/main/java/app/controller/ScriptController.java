package app.controller;

import app.script.Script;
import app.script.ScriptResource;
import app.script.ScriptStatus;
import app.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.SneakyThrows;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import static org.springframework.web.util.UriComponentsBuilder.*;

import javax.script.*;
import java.io.IOException;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping(value = "/api/scripts")
@ExposesResourceFor(Script.class)
public class ScriptController {
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
    @SneakyThrows
    public Resources<ScriptResource> getAllScripts() {
        return new Resources<>(
                assembler.toResources(service.getScripts()),
                linkTo(ScriptController.class).withSelfRel(),
                linkTo(ScriptController.class).slash("async")
                        .withRel("asyncExecution"),
                linkTo(ScriptController.class).slash("sync")
                        .withRel("syncExecution")
        );
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @JsonView(View.Rest.class)
    public ResponseEntity getScript(@PathVariable Integer id){
        return createResponseEntity(id, View.ViewType.Rest);
    }

    @GetMapping(value = "/{id}/body", produces = MediaTypes.HAL_JSON_VALUE)
    @JsonView(View.Body.class)
    public ResponseEntity getScriptBody(@PathVariable Integer id){
        return createResponseEntity(id, View.ViewType.Body);
    }

    @GetMapping(value = "/{id}/output", produces = MediaTypes.HAL_JSON_VALUE)
    @JsonView(View.Output.class)
    public ResponseEntity getScriptOutput(@PathVariable Integer id){
        return createResponseEntity(id, View.ViewType.Output);
    }

    @PostMapping(value = "/async", consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> asyncScriptEval(@RequestBody String body)
            throws ScriptException {
        Script script = service.compileAndSave(body);
        service.submitAsync(script);
        return ResponseEntity.created(fromPath("/api/scripts/{id}")
                .buildAndExpand(script.getId()).toUri()).build();
    }
    @PostMapping(value = "/sync", consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<StreamingResponseBody> syncScriptEval(@RequestBody String body)
            throws ScriptException {
        Script script = service.compileAndSave(body);
        return ResponseEntity.created(fromPath("/api/scripts/{id}")
                .buildAndExpand(script.getId()).toUri()).body(script);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity stopScriptExecution(@PathVariable Integer id) {
        return Optional.ofNullable(service.getScript(id))
                .map(script -> {
                    script.stopExecution();
                    service.delete(id);
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

    private ResponseEntity createResponseEntity(Integer id, View.ViewType type) {
        return Optional.ofNullable(service.getScript(id))
                .map(script -> ResponseEntity.ok(assembler.toResource(script)
                        .addLinksByRepresentationType(type)))
                .orElseThrow(IllegalArgumentException::new);
    }
}
