package app.service;

import app.script.Script;
import app.script.ScriptStatus;
import app.printer.AsyncPrint;
import app.printer.SyncPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import app.compiler.ScriptCompiler;

import javax.script.*;
import java.io.*;
import java.util.Collection;
import java.util.concurrent.*;


@Service
public class ScriptServiceImpl implements ScriptService {
    private final int NTHREADS = 10;
    private ConcurrentMap<Integer, Script> scripts =
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
    public void runAsynchronously(CompiledScript compiledScript,
                                  Script script,
                                  ResponseBodyEmitter emitter) {
        executorService.submit(() -> {
            try {
                createBindingsAndRun(compiledScript, script, emitter, null);
                emitter.complete();
            } catch (ScriptException se) {
                script.setStatus(ScriptStatus.Error);
                script.getOutput().append(se.getMessage());
                try {
                    emitter.send(se.getMessage());
                    emitter.complete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void runSynchronously(CompiledScript compiledScript,
                                 Script script,
                                 OutputStream out) throws IOException {
        try {
            createBindingsAndRun(compiledScript, script, null, out);
        } catch (ScriptException e) {
            script.setStatus(ScriptStatus.Error);
            script.getOutput().append(e.getMessage());
            out.write(e.getMessage().getBytes());
        }
    }

    @Override
    public boolean stopScriptExecution(Integer id) {
        Script script = scripts.get(id);
        if (script == null) {
            return false;
        } else {
            script.getThread().stop();
            scripts.remove(id);
            return true;
        }
    }

    @Override
    public Script getScript(Integer id) {
        return scripts.get(id);
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return compiler.compile(script);
    }

    @Override
    public void saveScript(Integer identifier, Script script) {
        scripts.put(identifier, script);
    }

    @Override
    public Collection<Script> getScripts() {
        return scripts.values();
    }

    private void createBindingsAndRun(CompiledScript compiledScript,
                                      Script script,
                                      ResponseBodyEmitter emitter,
                                      OutputStream out) throws ScriptException {
        script.setThread(Thread.currentThread());
        Bindings bindings = compiler.createBindings();
        if (out == null) bindings.put("print",
                new AsyncPrint(emitter, script.getOutput()));
        else bindings.put("print",
                new SyncPrint(out, script.getOutput()));
        script.setStatus(ScriptStatus.Running);
        long begin = System.currentTimeMillis();
        compiledScript.eval(bindings);
        long end = System.currentTimeMillis();
        script.setExecutionTime(end - begin);
        script.setStatus(ScriptStatus.Done);
    }
}

