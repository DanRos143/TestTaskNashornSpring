package app.service;

import app.script.Script;
import app.script.ScriptStatus;
import app.printer.AsyncPrint;
import app.printer.SyncPrint;
import jdk.nashorn.internal.runtime.ECMAException;
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
                script.setThread(Thread.currentThread());
                Bindings bindings = compiler.createBindings();
                bindings.put("print",
                        new AsyncPrint(emitter, script.getOutput()));
                script.setStatus(ScriptStatus.Running);
                compiledScript.eval(bindings);
                script.setStatus(ScriptStatus.Done);
                emitter.complete();
            } catch (ScriptException e) {
                script.setStatus(ScriptStatus.Error);
                script.getOutput().append(e.getMessage());
                try {
                    emitter.send(e.getMessage());
                    emitter.complete();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public void runSynchronously(CompiledScript compiledScript,
                                 Script script,
                                 OutputStream out) {
        try {
            script.setThread(Thread.currentThread());
            Bindings bindings = compiler.createBindings();
            bindings.put("print",
                    new SyncPrint(out, script.getOutput()));
            script.setStatus(ScriptStatus.Running);
            compiledScript.eval(bindings);
            script.setStatus(ScriptStatus.Done);
        } catch (ScriptException e) {
            script.setStatus(ScriptStatus.Error);
            script.getOutput().append(e.getMessage());
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

}

