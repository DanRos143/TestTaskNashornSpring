package app.service;

import app.script.Script;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import app.compiler.ScriptCompiler;

import javax.script.*;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
public class ScriptServiceImpl implements ScriptService {
    private ConcurrentMap<Integer, Script> scripts =
            new ConcurrentHashMap<>();
    private ScriptCompiler compiler;
    private AtomicInteger counter = new AtomicInteger(0);
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
    public Script getScript(Integer id) {
        return scripts.get(id);
    }

    @Override
    public Collection<Script> getScripts() {
        return scripts.values();
    }

    @Override
    public void submitAsync(Script script) {
        executor.submit(script::eval);
    }

    @Override
    public void delete(Integer id) {
        scripts.remove(id);
    }

    @Override
    public Script compileAndSave(String body) throws ScriptException {
        Script script = new Script(counter.incrementAndGet(), body, compiler.compile(body));
        scripts.put(script.getId(), script);
        return script;
    }
}

