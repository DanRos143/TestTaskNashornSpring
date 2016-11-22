package rest;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
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
    private ConcurrentMap<Integer, Future<String>> futures =
            new ConcurrentHashMap<>();
    private ScriptEvaluator evaluator = new ScriptEvaluator();

    @RequestMapping(value = "/api/scripts/{id}", method = RequestMethod.GET)
    public String getScript(@PathVariable Integer id,
                            HttpServletResponse response)
            throws ExecutionException, InterruptedException, IOException {
        PrintStream printStream = new PrintStream(response.getOutputStream(), true);
        System.setOut(printStream);
        response.addHeader("Connection", "close");
        String script = scripts.get(id).getContent();
        Callable<String> callable = () -> evaluator.evaluate(script);
        Future<String> future = pool.submit(callable);
        futures.put(id, future);
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

    public static void main(String[] args) {
        SpringApplication.run(ScriptEvalController.class, args);
    }
}
