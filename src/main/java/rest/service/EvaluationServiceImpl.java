package rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import rest.manager.ScriptManager;
import rest.manager.ScriptManagerImpl;
import rest.script.ScriptWrapper;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class EvaluationServiceImpl implements EvaluationService {

    private AtomicInteger counter = new AtomicInteger(0);
    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private ScriptManager evaluator;


    @Autowired
    public void setEvaluator(ScriptManager evaluator) {
        this.evaluator = evaluator;
    }

    public EvaluationServiceImpl() {
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public Future<?> runAsynchronously(String script, HttpServletResponse response) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(response.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setOut(printStream);
        int n = counter.incrementAndGet();
        response.setHeader("Location", "/api/scripts/" + n);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        Runnable r = () -> {
            try {
                scripts.put(n, new ScriptWrapper(script, Thread.currentThread()));
                new ScriptManagerImpl().compile(script).eval();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        };
        Future<?> future = executorService.submit(r);
        return future;
    }

    @Override
    public ResponseEntity runSynchronously(String script) {
        ResponseEntity responseEntity = null;
        try {
            scripts.put(counter.incrementAndGet(),
                    new ScriptWrapper(script, Thread.currentThread()));
            CompiledScript compiledScript = evaluator.compile(script);
            compiledScript.eval();
            ResponseEntity.created(
                    UriComponentsBuilder
                            .fromPath("/api/scripts/{id}")
                            .buildAndExpand(counter.get())
                            .toUri())
                    .build();
        } catch (ScriptException e) {
            responseEntity = ResponseEntity.badRequest().build();
        }
        return responseEntity;
    }

    @Override
    public ConcurrentMap<Integer, ScriptWrapper> getScriptWrappers() {
        return scripts;
    }

    @Override
    public AtomicInteger getCounter() {
        return counter;
    }

    @Override
    public ResponseEntity killScript(Integer id) {
        Thread current = scripts.get(id).getThread();
        if (current == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        else current.stop();
        return new ResponseEntity(HttpStatus.OK);

    }

}
