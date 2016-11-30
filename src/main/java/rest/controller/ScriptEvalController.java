package rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest.script.ScriptWrapper;
import rest.service.ScriptService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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

    @GetMapping
    public Iterable<ScriptWrapper> getLinks(){
        return service.getLinks("/api/scripts/");
    }

    @PostMapping
    public void scriptEval(@RequestParam(defaultValue = "false") boolean async,
                          @RequestBody String script,
                          HttpServletResponse response)
            throws IOException, ExecutionException, InterruptedException {
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        if (async) service.runAsynchronously(script, response).get();
        else service.runSynchronously(script, response);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity killScript(@PathVariable Integer id){
        return service.stopScriptExecution(id);
    }



}
