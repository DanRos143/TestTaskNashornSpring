package rest;

import jdk.nashorn.internal.codegen.CompilerConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by danros on 21.11.16.
 */

@RestController
@EnableAutoConfiguration
public class ScriptEvalController {
    private AtomicInteger counter = new AtomicInteger();
    ExecutorService pool =
            Executors.newCachedThreadPool();
    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();
    private ScriptEvaluator evaluator = new ScriptEvaluator();//?

    @RequestMapping(value = "/api/scripts/{id}", method = RequestMethod.GET)
    public void getScript(@PathVariable Integer id){
        Callable<String> callable = () -> {
            Thread.currentThread().setName("script" + id);
            System.out.println("inside callable");
            System.out.println(Thread.currentThread().getName());
            return evaluator.evaluate(scripts.get(id).getContent());
        };
        Future<String> future = pool.submit(callable);

    }

    @RequestMapping(value = "/api/scripts", method = RequestMethod.POST)
    public ResponseEntity<String> evaluateScript(@RequestBody String script){
        scripts.put(counter.incrementAndGet()
                , new ScriptWrapper(script));
        HttpHeaders headers = new HttpHeaders();
        headers.set("TrackURL", "/api/scripts/" + counter.get());
        System.out.println(Thread.currentThread().getName());
        return new ResponseEntity<>("Accepted", headers, HttpStatus.CREATED);
    }



    public static void main(String[] args) {
        SpringApplication.run(ScriptEvalController.class, args);
    }

}
/*

    @RequestMapping(value = "/api/scripts/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteScript(@PathVariable Integer id){
        scripts.remove(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }



     ExecutionException, InterruptedException, TimeoutException {
        System.out.println(Thread.currentThread().getName());
        PrintStream printStream = new PrintStream(out);
        System.setOut(printStream);

        String script = scripts.get(id).getContent();
        Callable<String> callable = () -> evaluator.evaluate(script);
        Future<String> future = pool.submit(callable);
        System.out.println(Thread.currentThread().getName());
 */