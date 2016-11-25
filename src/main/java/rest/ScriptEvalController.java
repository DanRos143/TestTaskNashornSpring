package rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriComponentsBuilder;
import sun.font.Script;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.io.OutputStream;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping(value = "/api/scripts/")
public class ScriptEvalController {
    AtomicInteger counter = new AtomicInteger(0);
    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();

    private ScriptEvaluator evaluator;

    @Autowired
    public void setEvaluator(ScriptEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @RequestMapping(method = RequestMethod.POST)
    public DeferredResult<ResponseEntity> acceptForExecution(@RequestBody String script,
                                                             @RequestParam boolean async)
            throws ExecutionException, InterruptedException {
        scripts.put(counter.incrementAndGet(),
                new ScriptWrapper(script));

        AsyncResult<ResponseEntity> asyncResult = new AsyncResult<>()

    }


    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ScriptWrapper getScript(@PathVariable Integer id){
        return scripts.get(id);
    }
}
/*

        try {
            evaluator.compile(script);
        } catch (ScriptException e) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.created(
                UriComponentsBuilder
                        .fromPath("/api/scripts/{id}")
                        .buildAndExpand(counter.get())
                        .toUri())
                .build();

 */
/*
Callable<ResponseEntity> callable = () -> {
            try {
                evaluator.compile(script);
            } catch (ScriptException e) {
                e.printStackTrace();
                return ResponseEntity.unprocessableEntity().build();
            }
            return ResponseEntity.created(
                    UriComponentsBuilder
                            .fromPath("/api/scripts/{id}")
                            .buildAndExpand(counter.get())
                            .toUri())
                    .build();
        };
        ListenableFuture<ResponseEntity> future =
                new ListenableFutureTask<ResponseEntity>(callable);
        System.out.println(future.get());
        return future.get();
 */