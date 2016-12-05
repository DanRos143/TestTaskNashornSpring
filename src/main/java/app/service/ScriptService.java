package app.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import app.script.ScriptWrapper;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;


public interface ScriptService {
    void runAsynchronously(CompiledScript compiledScript, ScriptWrapper sW, ResponseBodyEmitter emitter)
            throws IOException, ScriptException;

    void runSynchronously(CompiledScript script, ScriptWrapper sW, OutputStream out)
            throws IOException, ScriptException;

    ResponseEntity stopScriptExecution(Integer id);

    ScriptWrapper getScriptInfo(Integer scriptId);

    CompiledScript compile(String script) throws ScriptException;

    void saveResource(Integer identifier, ScriptWrapper scriptWrapper);

    Collection<ScriptWrapper> getScriptWrappers();
}
