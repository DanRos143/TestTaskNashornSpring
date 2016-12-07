package app.service;

import app.script.Script;
import app.script.ScriptStatus;
import app.writer.SyncWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import app.compiler.ScriptCompiler;

import javax.script.*;
import java.io.*;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.*;


@Service
public class ScriptServiceImpl implements ScriptService {
    //@Value("${numberOfThreads}")
    private int NTHREADS = 10;
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
                                  Script script) {
        /*executorService.submit(() -> {
            try {
                createBindingsAndRun(compiledScript, script, null);
            } catch (ScriptException se) {
                script.setStatus(ScriptStatus.Error);
                script.getOutput().append(se.getMessage());
            }
        });*/
    }

    @Override
    public void runSynchronously(CompiledScript compiledScript,
                                 Script script,
                                 OutputStream out) throws IOException {
        try {
            createContextAndRun(compiledScript, script, out, false);
        } catch (ScriptException e) {
            script.setStatus(ScriptStatus.Error);
            out.write(e.getMessage().getBytes());
        }
    }

    @Override
    public boolean stopScriptExecution(Integer id) {//fix
        return Optional.ofNullable(scripts.get(id))
                .map(script -> {
                    script.stopScriptExecution();
                    return true;
                })
                .orElse(false);
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

    private void createContextAndRun(CompiledScript compiledScript,
                                      Script script,
                                      OutputStream out, boolean async) throws ScriptException {
        script.setThread(Thread.currentThread());
        script.setStatus(ScriptStatus.Running);
        ScriptContext ctx = new SimpleScriptContext();
        if (!async) ctx.setWriter(new SyncWriter(out, script.getOutput()));
        long begin = System.currentTimeMillis();
        compiledScript.eval(ctx);
        long end = System.currentTimeMillis();
        script.setExecutionTime(end - begin);
        script.setStatus(ScriptStatus.Done);
    }
}

