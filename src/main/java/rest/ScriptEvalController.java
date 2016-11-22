package rest;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


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
                            HttpServletResponse response, HttpServletRequest request)
            throws ExecutionException, InterruptedException, IOException {

        PrintStream printStream = new PrintStream(response.getOutputStream(), true);
        System.setOut(printStream);

        response.setHeader("Keep-Alive", "timeout=1");
        String script = scripts.get(id).getContent();
        Callable<String> callable = () -> {
            scripts.get(id).setStatus(Status.Running);
            return evaluator.evaluate(script);
        };
        Future<String> future = pool.submit(callable);
        futures.put(id, future);
        BufferedReader bf = new BufferedReader(request.getReader());
        while (!future.isDone()){
            if (bf.read() == -1){
                System.out.println("The client is dead");
            }
        }
        if (future.isDone()){
            scripts.get(id).setStatus(Status.Done);
            futures.remove(id);
        }
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
    @RequestMapping(value = "/api/scripts/{id}/status")
    public Status getStatus(@PathVariable Integer id){
        return scripts.get(id).getStatus();
    }
    @RequestMapping(value = "/api/scripts/{id}/output")
    public String getOutput(@PathVariable Integer id){
        return scripts.get(id).getOutput();
    }

    @RequestMapping(value = "/api/scripts", method = RequestMethod.GET)
    public Collection<ScriptWrapper> getAllScripts(){
        return scripts.values();
    }

    public static void main(String[] args) {
        SpringApplication.run(ScriptEvalController.class, args);
    }
}

/*while (!future.isDone()){
            System.out.println(request.getInputStream().read());
            TimeUnit.SECONDS.sleep(1);
        }
        if (future.isDone()){
                scripts.get(id).setStatus(Status.Done);
                futures.remove(id);
                System.out.println(Thread.currentThread().getName());
                }
 */