package app.service;

import app.script.Script;
import app.script.ScriptStatus;
import app.writer.SyncWriter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.StringBuilderWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import app.compiler.ScriptCompiler;

import javax.script.*;
import java.io.*;
import java.util.Collection;
import java.util.concurrent.*;

@Log4j2
@Service
public class ScriptServiceImpl implements ScriptService {
    private ConcurrentMap<Integer, Script> scripts =
            new ConcurrentHashMap<>();
    private ScriptCompiler compiler;
    private AsyncTaskExecutor executor;

    @Autowired
    public void setExecutor(AsyncTaskExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setCompiler(ScriptCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public void runAsync(CompiledScript compiledScript, Script script) {
        log.trace("starting async execution");
        executor.submit(() -> {
            try {
                compiledScript.eval(createContext(script, null))
            } catch (ScriptException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void runSync(CompiledScript compiledScript,
                                 Script script) throws IOException, ScriptException {
        //call method

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
    public void saveScript(Integer id, Script script) {
        scripts.put(id, script);
    }

    @Override
    public Collection<Script> getScripts() {
        return scripts.values();
    }

    private ScriptContext createContext(Script script,OutputStream out){
        script.setThread(Thread.currentThread());
        ScriptContext ctx = new SimpleScriptContext();
        if (out == null) ctx.setWriter(new StringBuilderWriter(script.getOutput()));
        else ctx.setWriter(new SyncWriter(out, script.getOutput()));
        return ctx;
    }

    /*private void calculateTimeAndUpdateStatus(Script script, CompiledScript compiledScript){
        try {
            long begin = System.currentTimeMillis();
            script.setStatus(ScriptStatus.Running);
            compiledScript.eval(createContext(script, null));
            long end = System.currentTimeMillis();
            script.setExecutionTime(end - begin);
            script.setStatus(ScriptStatus.Done);
        } catch (ScriptException e) {
            script.getOutput().append(e.getMessage());
            script.setStatus(ScriptStatus.Error);
        }
    }*/
}

