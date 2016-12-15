package app.controller;

import app.script.Script;
import app.script.ScriptResource;
import app.service.ScriptService;
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

import javax.script.ScriptException;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

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
    public void setAssembler(ResourceAssemblerSupport<Script, ScriptResource> assembler) {
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @SneakyThrows
    public Resources<ScriptResource> getAllScripts() {
        return new Resources<>(assembler.toResources(service.getScripts()),
                linkTo(ScriptController.class).withSelfRel(),
                linkTo(methodOn(ScriptController.class).evalScript(true, null))
                        .withRel("asyncEval"),
                linkTo(methodOn(ScriptController.class).evalScript(false, null))
                        .withRel("syncEval"));
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<ScriptResource> getScriptRepresentation(@PathVariable Integer id) {
        return Optional.ofNullable(service.getScript(id))
                .map(script -> ResponseEntity.ok(assembler.toResource(script).buildLinks()))
                .orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping(value = "/{id}/body", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<String> getScriptBody(@PathVariable Integer id) {
        return Optional.ofNullable(service.getScript(id))
                .map(script -> ResponseEntity.ok(script.getBody()))
                .orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping(value = "/{id}/output", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<String> getScriptOutput(@PathVariable Integer id) {
        return Optional.ofNullable(service.getScript(id))
                .map(script -> ResponseEntity.ok(script.getOutput().toString()))
                .orElseThrow(IllegalArgumentException::new);
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Script> evalScript(@RequestParam(defaultValue = "false") boolean async,
                                             @RequestBody String body) throws ScriptException {
        Script script = service.compileAndSave(body);
        if (async) {
            log.info("starting async evaluation");
            service.submitAsync(script);
            return ResponseEntity.created(fromPath("/api/scripts/{id}")
                    .buildAndExpand(script.getId()).toUri()).body(null);
        } else {
            log.info("starting sync evaluation");
            return ResponseEntity.created(fromPath("/api/scripts/{id}")
                    .buildAndExpand(script.getId()).toUri()).body(script);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> stopScriptExecution(@PathVariable Integer id) {
        return Optional.ofNullable(service.getScript(id))
                .map(script -> {
                    log.info("stopping script execution");
                    script.stopExecution();
                    service.delete(id);
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(IllegalArgumentException::new);
    }
}
