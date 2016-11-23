package rest;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


@RestController
@EnableAutoConfiguration
public class ScriptEvalController {
    AtomicInteger counter = new AtomicInteger(0);
    ExecutorService executorService =
            Executors.newCachedThreadPool();
    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();
    private Map<Integer, Future<String>> futures =
            new HashMap<>();
    private ReentrantLock lock = new ReentrantLock();
    ScriptEvaluator evaluator = new ScriptEvaluator(lock);



    @RequestMapping(value = "/api/scripts/{id}")
    public DeferredResult<String> test(@PathVariable Integer id, OutputStream out, InputStream in) throws ExecutionException, InterruptedException{
        DeferredResult<String> deferredResult = new DeferredResult<>();

        PrintStream printStream = new PrintStream(out, true);
        System.setOut(printStream);

        Callable<DeferredResult<String>> task = () -> {
            //System.out.println("Result");
            scripts.get(id).setStatus(Status.Running);
            String result = evaluator.evaluate(scripts.get(id).getContent());
            deferredResult.setResult(result);
            return deferredResult;
        };
        Future<DeferredResult<String>> future = executorService.submit(task);
        while (!future.isDone()){
            try {
                out.write((char) 0);
                out.flush();
            } catch (IOException e) {
                scripts.get(id).setStatus(Status.Interrupted);
                System.out.println(e.getLocalizedMessage());
                future.cancel(true);
            }
        }
        return future.get();
    }
    @RequestMapping(value = "/api/scripts", method = RequestMethod.POST)
    public ResponseEntity<String> saveScript(@RequestBody String script){
        scripts.put(counter.incrementAndGet(),
                new ScriptWrapper(script));
        HttpHeaders headers = new HttpHeaders();
        headers.set("TrackURL", "/api/scripts/" + counter.get());
        return new ResponseEntity<>("Accepted\n", headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/scripts/{id}/status")
    public Status getStatus(@PathVariable Integer id){
        return scripts.get(id).getStatus();
    }


    public static void main(String[] args) {
        SpringApplication.run(ScriptEvalController.class, args);
    }
}
/*
private AtomicInteger counter = new AtomicInteger();
    ExecutorService pool =
            Executors.newCachedThreadPool();
    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Future<String>> futures =
            new ConcurrentHashMap<>();
    private ReentrantLock lock = new ReentrantLock();
    ScriptEvaluator evaluator = new ScriptEvaluator(lock);

    @RequestMapping(value = "/api/scripts/{id}", method = RequestMethod.GET)
    public String getScript(@PathVariable Integer id,
                            HttpServletResponse response, HttpServletRequest request)
            throws InterruptedException, IOException, ExecutionException {
        System.out.println("greet");
        OutputStream out = response.getOutputStream();
        InputStream in = request.getInputStream();
        PrintStream printStream = new PrintStream(out, true);
        System.setOut(printStream);
        Callable<String> task = () -> {
            scripts.get(id).setStatus(Status.Running);
            return evaluator.evaluate(scripts.get(id).getContent());
        };
        Future<String> future = pool.submit(task);
        return future.get();
    }

    @RequestMapping(value = "/api/scripts", method = RequestMethod.POST)
    public ResponseEntity<String> evaluateScript(@RequestBody String script){
        scripts.put(counter.incrementAndGet(),
                new ScriptWrapper(script));
        HttpHeaders headers = new HttpHeaders();
        headers.set("TrackURL", "/api/scripts/" + counter.get());
        return new ResponseEntity<>("Accepted\n", headers, HttpStatus.CREATED);
    }
    @RequestMapping(value = "/api/scripts/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteScript(@PathVariable Integer id){
        System.out.println("Canceling");
        futures.get(id).cancel(true);
        scripts.remove(id);
        return new ResponseEntity<>("Deleted\n", HttpStatus.OK);
    }

    @RequestMapping(value = "/api/scripts/{id}/output")
    public String getOutput(@PathVariable Integer id){
        return scripts.get(id).getOutput();
    }

    @RequestMapping(value = "/api/scripts", method = RequestMethod.GET)
    public Collection<ScriptWrapper> getAllScripts(){
        return scripts.values();
    }
 */
