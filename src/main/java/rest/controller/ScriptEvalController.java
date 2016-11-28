package rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest.manager.ScriptManagerImpl;
import rest.service.EvaluationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.*;


@RestController
@RequestMapping(value = "/api/scripts/")
public class ScriptEvalController {

    private EvaluationService service;

    @Autowired
    public void setService(EvaluationService service) {
        this.service = service;
    }

    @PostMapping("async")
    public void asyncEval(@RequestBody String script, HttpServletResponse response, HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        PrintStream printStream = new PrintStream(response.getOutputStream(), true);
        response.setHeader("Location", "/api/scripts/" + 0);
        response.setStatus(202);
        System.setOut(printStream);
        Callable<Object> callable = () -> {
            new ScriptManagerImpl().evaluate(script);
            return new Object();
        };
        Future<Object> future = executorService.submit(callable);
        future.get();


    }

    @PostMapping("sync")
    public void syncEval(HttpServletResponse response, HttpServletRequest request, @RequestBody String script) throws IOException {
        service.runSynchronously(script);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity killScript(@PathVariable Integer id){
        return service.killScript(id);
    }
    @GetMapping
    public Iterable<String> getLinks(){
        return null;
    }

}
/*
Callable<Object> callable = () -> {
            new ScriptManagerImpl().evaluate(script);
            return new Object();
        };
        Future<Object> future = executorService.submit(callable);
        future.get();

 */