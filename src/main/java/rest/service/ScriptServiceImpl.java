package rest.service;

import jdk.nashorn.internal.runtime.ParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.util.UriComponentsBuilder;
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
    public void runAsynchronously(String script, ResponseBodyEmitter emitter)
            throws IOException, ScriptException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(baos, true);
        Callable<Void> callable = () -> {

            return null;
        };
        //executorService.submit()
        /*Integer id = counter.incrementAndGet();
        ResponseEntity.created(UriComponentsBuilder
                .fromPath("/api/scripts/{id}")
                .buildAndExpand(id)
                .toUri())
                .build();*/
    }

    @Override
    public void runSynchronously(String script, PrintWriter printWriter) throws ScriptException {
        CompiledScript compiledScript = compiler.compile(script);
        Integer id = counter.incrementAndGet();

        ScriptWrapper scriptWrapper = new ScriptWrapper(id,script);
        scriptWrapper.setStatus(ScriptStatus.Running);
        scriptWrapper.setThread(Thread.currentThread());
        scripts.put(id, scriptWrapper);

        ScriptContext scriptContext = new SimpleScriptContext();
        scriptContext.setWriter(printWriter);
        compiledScript.eval(scriptContext);
        scriptWrapper.setStatus(ScriptStatus.Done);
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
    public Set<String> getLinks(String path) {//hardcoded!!!
        Set<String> links = new HashSet<>();
        scripts.keySet().forEach(integer -> links.add(path + integer));
        return links;
    }

    @Override
    public ScriptWrapper getScriptInfo(Integer scriptId) {
        return scripts.get(scriptId);
    }
}

