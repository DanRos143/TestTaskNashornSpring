package rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


@RestController
public class ScriptEvalController {
    AtomicInteger counter = new AtomicInteger(0);
    ExecutorService executorService =
            Executors.newWorkStealingPool();

    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Future<String>> futures =
            new ConcurrentHashMap<>();
    private ReentrantLock lock = new ReentrantLock();
    ScriptEvaluator evaluator = new ScriptEvaluator();



    @RequestMapping(value = "/api/scripts/{id}")
    public String test(@PathVariable Integer id, OutputStream out, InputStream in)
            throws ExecutionException{
        PrintStream printStream = new PrintStream(out, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream copyStream = new PrintStream(baos, true);
        System.setOut(copyStream);

        Callable<String> task = () -> {
            scripts.get(id).setStatus(Status.Running);
            return evaluator.evaluate(scripts.get(id).getContent());
        };
        Future<String> future;
        try {
            lock.lock();
            future = executorService.submit(task);
            futures.put(id, future);
            while (!future.isDone()){
                try {
                    out.flush();//exception if client is dead
                    TimeUnit.SECONDS.sleep(1);
                    scripts.get(id).getOutput().append(baos.toString());
                    printStream.print(baos.toString());
                } catch (IOException e) {
                    printStream.print(e.getLocalizedMessage());
                    scripts.get(id).setStatus(Status.Interrupted);
                    future.cancel(true);
                } catch (InterruptedException e) {
                    printStream.print(e.getLocalizedMessage());
                }
            }
        } finally {
            lock.unlock();
        }
        String result = "";
        try {
            result = future.get();
        } catch (InterruptedException | CancellationException e) {
            printStream.println(e.getLocalizedMessage());
        }
        return result;


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
    @RequestMapping(value = "/api/scripts/{id}/output")
    public String getOutput(@PathVariable Integer id){
        return scripts.get(id).getOutput().toString();
    }

    @RequestMapping(value = "/api/scripts/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteScript(@PathVariable Integer id){
        System.out.println("Canceling");
        futures.get(id).cancel(true);
        scripts.get(id).setStatus(Status.Interrupted);
        return new ResponseEntity<>("Deleted\n", HttpStatus.OK);
    }
    @RequestMapping(value = "/api/scripts", method = RequestMethod.GET)
    public Collection<ScriptWrapper> getAllScripts(){
        return scripts.values();
    }


}
