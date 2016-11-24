package rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
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
    private ConcurrentMap<Integer, Future<String>> futures =
            new ConcurrentHashMap<>();
    private ReentrantLock lock = new ReentrantLock();
    ScriptEvaluator evaluator = new ScriptEvaluator(lock);



    @RequestMapping(value = "/api/scripts/{id}")
    public String test(@PathVariable Integer id, OutputStream out, InputStream in) throws ExecutionException, InterruptedException{

        PrintStream printStream = new PrintStream(out, true);
        System.setOut(printStream);
        Callable<String> task = () -> {
            scripts.get(id).setStatus(Status.Running);
            return evaluator.evaluate(scripts.get(id).getContent());
        };
        Future<String> future = executorService.submit(task);
        System.out.println(futures.get(id) == null);
        futures.put(id, future);
        while (!future.isDone()){
            try {
                out.write((char) 0);
                out.flush();
                //System.out.println("conn");
                if (future.isCancelled()) throw new IOException();
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                scripts.get(id).setStatus(Status.Interrupted);
                System.out.println(e.getLocalizedMessage());
                future.cancel(true);
            }
        }
        //scripts.get(id).setStatus(Status.Done);
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

    @RequestMapping(value = "/api/scripts/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteScript(@PathVariable Integer id){
        System.out.println("Canceling");
        futures.get(id).cancel(true);
        //scripts.get(id).setStatus(Status.Interrupted);
        scripts.remove(id);
        return new ResponseEntity<>("Deleted\n", HttpStatus.OK);
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
/*try {
                out.write((char) 0);
                out.flush();
                //in.read();
            } catch (IOException e) {
                scripts.get(id).setStatus(Status.Interrupted);
                System.out.println(e.getLocalizedMessage());
                future.cancel(true);
            }*/