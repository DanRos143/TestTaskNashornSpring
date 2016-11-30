package rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rest.compiler.ScriptCompiler;
import rest.script.ScriptStatus;
import rest.script.ScriptWrapper;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
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
    public Future<?> runAsynchronously(String script, HttpServletResponse response) throws IOException, ScriptException {
        Integer id = counter.incrementAndGet();
        PrintWriter printWriter = response.getWriter();
        ScriptWrapper scriptWrapper = new ScriptWrapper(script);
        CompiledScript compiledScript = compiler.compile(script);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.setHeader("Location", location + id);
        Runnable r = () -> {
            try {
                scriptWrapper.setThread(Thread.currentThread());
                scriptWrapper.setStatus(ScriptStatus.Running);
                scripts.put(id, scriptWrapper);
                ScriptContext scriptContext = new SimpleScriptContext();
                scriptContext.setWriter(printWriter);
                compiledScript.eval(scriptContext);
                scriptWrapper.setStatus(ScriptStatus.Done);
            } catch (ScriptException e) {
                printWriter.print(e.getLocalizedMessage());
            }
        };
        return executorService.submit(r);
    }

    @Override
    public void runSynchronously(String script, HttpServletResponse response) throws IOException, ScriptException {
        Integer id = counter.incrementAndGet();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(baos, true);
        ScriptWrapper scriptWrapper = new ScriptWrapper(script);
        PrintWriter out = response.getWriter();
        CompiledScript compiledScript = compiler.compile(script);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.setHeader("Location", location + id);
        Future<?> future = executorService.submit(() -> {
            try {
                scriptWrapper.setStatus(ScriptStatus.Running);
                scriptWrapper.setThread(Thread.currentThread());
                scripts.put(id, scriptWrapper);
                ScriptContext scriptContext = new SimpleScriptContext();
                scriptContext.setWriter(printWriter);
                compiledScript.eval(scriptContext);
                scriptWrapper.setStatus(ScriptStatus.Done);
            } catch (ScriptException e) {
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
        if (!future.isDone()){
            scriptWrapper.setStatus(ScriptStatus.Dead);
            scriptWrapper.getThread().stop();
        }
    }

    @Override
    public ResponseEntity stopScriptExecution(Integer id) {
        ResponseEntity responseEntity;
        ScriptWrapper sw = scripts.get(id);
        if (sw == null) {
            responseEntity = new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            sw.getThread().stop();
            responseEntity = new ResponseEntity(HttpStatus.OK);
        }
        return responseEntity;
    }

    @Override
    public Set<String> getLinks(String path) {
        Set<String> links = new HashSet<>();
        scripts.keySet().forEach(integer -> links.add(path + integer));
        return links;
    }

    @Override
    public ScriptWrapper getScriptInfo(Integer scriptId) {
        return scripts.get(scriptId);
    }
}

