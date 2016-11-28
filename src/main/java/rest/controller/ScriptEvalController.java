package rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriComponentsBuilder;
import rest.evaluator.ScriptEvaluator;
import rest.script.ScriptWrapper;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping(value = "/api/scripts/")
public class ScriptEvalController {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    AtomicInteger counter = new AtomicInteger(0);
    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();

    ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");

    private ScriptEvaluator evaluator;

    @Autowired
    public void setEvaluator(ScriptEvaluator evaluator) {
        this.evaluator = evaluator;
    }


    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ScriptWrapper getScript(@PathVariable Integer id){
        return scripts.get(id);
    }


    @PostMapping("true")
    public DeferredResult<ResponseEntity> evaluateScriptAsynchronously(@RequestBody String script, HttpServletResponse response) throws IOException, InterruptedException {
        DeferredResult<ResponseEntity> deferred = new DeferredResult<>();
        //System.setOut(new PrintStream(response.getOutputStream()));
        Future<ResponseEntity> future = executorService.submit(() -> {
            nashorn.eval(script);
            return new ResponseEntity(HttpStatus.OK);
        });
        scripts.put(counter.incrementAndGet(), new ScriptWrapper(script, future));
        TimeUnit.SECONDS.sleep(1);
        future.cancel(true);
        return deferred;
    }
    @PostMapping("false")
    public void evaluateScriptSynchronously(HttpServletResponse response, HttpServletRequest request, @RequestBody String script) throws IOException {
        System.out.println(Thread.currentThread().getName());
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.println(ResponseEntity.created(UriComponentsBuilder.fromPath("/evaluator/{id}").buildAndExpand(counter.get()).toUri()).build().toString());
        try {

            evaluator.evaluate(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity killScript(@PathVariable Integer id){
        ResponseEntity responseEntity = null;
        if (scripts.get(id) == null) responseEntity = new ResponseEntity(HttpStatus.NOT_FOUND);
        else {
            scripts.get(id).getFuture().cancel(true);
        }
        return responseEntity;
    }



}
