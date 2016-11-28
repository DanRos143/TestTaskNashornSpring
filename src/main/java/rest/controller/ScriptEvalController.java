package rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest.manager.ScriptManagerImpl;
import rest.script.ScriptWrapper;
import rest.service.EvaluationService;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping(value = "/api/scripts/")
public class ScriptEvalController {
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    private AtomicInteger counter = new AtomicInteger(0);
    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();

    private EvaluationService service;

    @Autowired
    public void setService(EvaluationService service) {
        this.service = service;
    }

    @PostMapping("async")
    public void asyncEval(@RequestBody String script, HttpServletResponse response, HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {

        PrintStream printStream = new PrintStream(response.getOutputStream(), true);
        //just for test
        response.setStatus(202);
        System.setOut(printStream);
        Runnable r = () -> {
            try {
                int n = counter.incrementAndGet();
                scripts.put(n, new ScriptWrapper(script, Thread.currentThread()));
                response.setHeader("Location", "/api/scripts/" + n);
                new ScriptManagerImpl().evaluate(script);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        };
        Future<?> future = executorService.submit(r);
        future.get();
    }

    @PostMapping("sync")
    public void syncEval(OutputStream out, @RequestBody String script) throws IOException {
        PrintStream printStream = new PrintStream(out, true);
        //response.setStatus(202);
        //System.setOut(printStream);
        int n = counter.incrementAndGet();
        executorService.submit(() -> {
            scripts.put(n, new ScriptWrapper(script, Thread.currentThread()));
            //response.setHeader("Location", "/api/scripts/" + n);
            try {
                new ScriptManagerImpl().evaluate(script);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        });
        while (scripts.get(n).getThread().isAlive()){
            try {
                out.write((char) 0);
                out.flush();
            } catch (IOException e) {
                scripts.get(n).getThread().stop();
            }
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity killScript(@PathVariable Integer id){
        ResponseEntity responseEntity = null;
        ScriptWrapper sw = scripts.get(id);
        if (sw == null) responseEntity = new ResponseEntity(HttpStatus.NOT_FOUND);
        else {
            sw.getThread().stop();
            responseEntity = new ResponseEntity(HttpStatus.OK);
        }
        return responseEntity;
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