package rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import rest.compiler.ScriptCompiler;
import rest.compiler.ScriptCompilerImpl;
import rest.script.ScriptStatus;
import rest.script.ScriptWrapper;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class ScriptServiceImpl implements ScriptService {
    private AtomicInteger counter = new AtomicInteger(0);
    private ConcurrentMap<Integer, ScriptWrapper> scripts =
            new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private ScriptCompiler compiler;

    public ScriptServiceImpl() {
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Autowired
    public void setCompiler(ScriptCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public Future<?> runAsynchronously(String script, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        int id = counter.incrementAndGet();
        ScriptWrapper scriptWrapper = new ScriptWrapper(script);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.setHeader("Location", "/api/scripts/" + id);
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
                printWriter.print(e.getLocalizedMessage());
            }
        };
        return executorService.submit(r);
    }

    @Override
    public void runSynchronously(String script, HttpServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(baos, true);
        ScriptWrapper scriptWrapper = new ScriptWrapper(script);
        int id = counter.incrementAndGet();
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.setHeader("Location", "/api/scripts/" + id);
        Future<?> future = executorService.submit(() -> {
            scriptWrapper.setStatus(ScriptStatus.Running);
            scriptWrapper.setThread(Thread.currentThread());
            scripts.put(id, scriptWrapper);
            try {
                ScriptContext scriptContext = new SimpleScriptContext();
                scriptContext.setWriter(printWriter);
                compiler.compile(script).eval(scriptContext);
            } catch (ScriptException e) {
                printWriter.print(e.getLocalizedMessage());
            }
        });
        while (true){
            try {
                if (future.isDone()) {
                    scripts.get(id).setStatus(ScriptStatus.Done);
                    break;
                }
                out.write(baos.toByteArray());
                out.flush();
                baos.reset();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
                scriptWrapper.setStatus(ScriptStatus.Dead);
                scriptWrapper.getThread().stop();
                break;
            }
        }
    }

    @Override
    public ConcurrentMap<Integer, ScriptWrapper> getScriptWrappers() {
        return scripts;
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
