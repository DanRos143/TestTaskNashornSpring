package app.service;

import app.script.ScriptStatus;
import app.util.AsyncPrint;
import app.util.SyncPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import app.compiler.ScriptCompiler;
import app.script.ScriptWrapper;

import javax.script.*;
import java.io.*;
import java.util.Collection;
import java.util.concurrent.*;


@Service
public class ScriptServiceImpl implements ScriptService {
    private final int NTHREADS = 10;
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
    public void runAsynchronously(CompiledScript compiledScript, ScriptWrapper sW, ResponseBodyEmitter emitter)
            throws IOException {
        executorService.submit(() -> {
            try {
                sW.setThread(Thread.currentThread());
                Bindings bindings = compiler.createBindings();
                bindings.put("print", new AsyncPrint(emitter, sW.getOutput()));
                sW.setStatus(ScriptStatus.Running);
                compiledScript.eval(bindings);
            } catch (ScriptException e) {
                sW.setStatus(ScriptStatus.Error);
                emitter.completeWithError(e);
            }
            sW.setStatus(ScriptStatus.Done);
            emitter.complete();
        });
    }

    @Override
    public void runSynchronously(CompiledScript compiledScript,ScriptWrapper sW, OutputStream out) {
        try {
            sW.setThread(Thread.currentThread());
            Bindings bindings = compiler.createBindings();
            bindings.put("print", new SyncPrint(out, sW.getOutput()));
            sW.setStatus(ScriptStatus.Running);
            compiledScript.eval(bindings);
        } catch (ScriptException e) {
            sW.setStatus(ScriptStatus.Error);
            e.printStackTrace();
        }
        sW.setStatus(ScriptStatus.Done);
    }

    @Override
    public ResponseEntity stopScriptExecution(Integer id) {
        ResponseEntity responseEntity;
        ScriptWrapper sw = scripts.get(id);
        if (sw == null) {
            responseEntity = ResponseEntity.notFound().build();
        } else {
            sw.getThread().stop();
            scripts.remove(id);
            responseEntity = ResponseEntity.ok().build();
        }
        return responseEntity;
    }

    @Override
    public ScriptWrapper getScriptInfo(Integer scriptId) {
        return scripts.get(scriptId);
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return compiler.compile(script);
    }

    @Override//?? duplicated id
    public void saveResource(Integer identifier, ScriptWrapper scriptWrapper) {
        scripts.put(identifier, scriptWrapper);
    }

    @Override
    public Collection<ScriptWrapper> getScriptWrappers() {
        return scripts.values();
    }


}

