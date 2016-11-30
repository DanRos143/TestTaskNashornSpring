package rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rest.compiler.ScriptCompiler;
import rest.script.ScriptStatus;
import rest.script.ScriptWrapper;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class ScriptServiceImpl implements ScriptService {
    private final int NTHREADS = 10;
    private String location = "/api/scripts/";
    private AtomicInteger counter = new AtomicInteger(0);
    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private ScriptCompiler compiler;


    public ScriptServiceImpl() {
        this.executorService = Executors.newFixedThreadPool(NTHREADS);
    }

    @Autowired
    public void setCompiler(ScriptCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public Future<?> runAsynchronously(String script, HttpServletResponse response) throws IOException {
        Integer id = counter.incrementAndGet();
        response.setHeader("Location", location + id);
        PrintWriter printWriter = response.getWriter();
        ScriptWrapper scriptWrapper = new ScriptWrapper(script);
        Runnable r = () -> {
            try {
                scriptWrapper.setThread(Thread.currentThread());
                scriptWrapper.setStatus(ScriptStatus.Running);
                scripts.put(id, scriptWrapper);
                ScriptContext scriptContext = new SimpleScriptContext();
                scriptContext.setWriter(printWriter);
                compiler.compile(script).eval(scriptContext);
                scriptWrapper.setStatus(ScriptStatus.Done);
            } catch (ScriptException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                printWriter.print(e.getLocalizedMessage());
            }
        };
        return executorService.submit(r);
    }

    @Override
    public void runSynchronously(String script, HttpServletResponse response) throws IOException {
        Integer id = counter.incrementAndGet();
        response.setHeader("Location", location + id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(baos, true);
        ScriptWrapper scriptWrapper = new ScriptWrapper(script);
        PrintWriter out = response.getWriter();
        Future<?> future = executorService.submit(() -> {
            try {
                scriptWrapper.setStatus(ScriptStatus.Running);
                scriptWrapper.setThread(Thread.currentThread());
                scripts.put(id, scriptWrapper);
                ScriptContext scriptContext = new SimpleScriptContext();
                scriptContext.setWriter(printWriter);
                compiler.compile(script).eval(scriptContext);
                scriptWrapper.setStatus(ScriptStatus.Done);
            } catch (ScriptException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                printWriter.print(e.getLocalizedMessage());
            }
        });
        while (!out.checkError()){
            if (future.isDone()) {
                scripts.get(id).setStatus(ScriptStatus.Done);
                break;
            }
            out.print(baos.toString());
            baos.reset();
            out.flush();
        }
        scriptWrapper.setStatus(ScriptStatus.Dead);
        scriptWrapper.getThread().stop();
    }

    @Override
    public ResponseEntity stopScriptExecution(Integer id) {
        ResponseEntity responseEntity;
        ScriptWrapper sw = scripts.get(id);
        if (sw == null) responseEntity = new ResponseEntity(HttpStatus.NOT_FOUND);
        else {
            sw.getThread().stop();
            responseEntity = new ResponseEntity(HttpStatus.OK);
        }
        return responseEntity;
    }

    @Override
    public Iterable<ScriptWrapper> getLinks(String path) {
        scripts.forEach((integer, scriptWrapper) -> scriptWrapper.setLocation(path + integer));
        return scripts.values();
    }
}

