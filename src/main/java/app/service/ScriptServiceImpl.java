package app.service;

import app.script.Script;
import app.writer.TeeWriter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.StringBuilderWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import app.compiler.ScriptCompiler;

import javax.script.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public Script getScript(Integer id) {
        return scripts.get(id);
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return compiler.compile(script);
    }

    @Override
    public Collection<Script> getScripts() {
        return scripts.values();
    }

    @Override
    public void submitAsync(Script script) {
        executor.submit(script::runAsync);
    }

    @Override
    public void saveScript(Script script) {
        scripts.put(script.getId(), script);
    }

}

