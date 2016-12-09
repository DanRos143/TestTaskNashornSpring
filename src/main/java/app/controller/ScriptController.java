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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import static org.springframework.web.util.UriComponentsBuilder.*;

import javax.script.*;
import java.util.Optional;
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
    @SneakyThrows
    public Resources<ScriptResource> getAllScripts() {
        return new Resources<>(
                assembler.toResources(service.getScripts()),
                linkTo(ScriptController.class).withSelfRel(),
                linkTo(methodOn(ScriptController.class).evalScript(null, true))
                        .withRel("asyncExecution"),
                linkTo(methodOn(ScriptController.class).evalScript(null, false))
                        .withRel("syncExecution")
        );
    }
    //hardcoded constants for view classes. Use class names instead?
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @JsonView(View.Rest.class)
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

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity evalScript(@RequestBody String body,
                                     @RequestParam(defaultValue = "false") boolean async)
            throws ScriptException {
        CompiledScript compiled = service.compile(body);
        Script script = new Script(counter.incrementAndGet(), body, compiled);
        service.saveScript(script);
        if (async) {
            service.submitAsync(script);
            return ResponseEntity.created(fromPath("/api/scripts/{id}")
                    .buildAndExpand(script.getId())
                    .toUri())
                    .build();
        } else {
            return ResponseEntity.created(fromPath("/api/scripts/{id}")
                    .buildAndExpand(script.getId()).toUri()).body(script);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity stopScriptExecution(@PathVariable Integer id) {
        return Optional.ofNullable(service.getScript(id))
                .map(script -> {
                    script.stopExecution();
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

    private ResponseEntity createResponseEntity(Integer id, String type){//hardcoded constants
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