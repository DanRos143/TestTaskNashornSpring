package app.service;

import app.script.Script;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.io.OutputStream;
import java.util.Collection;


public interface ScriptService {
    void runAsynchronously(CompiledScript compiledScript,
                           Script script,
                           ResponseBodyEmitter emitter);
    void runSynchronously(CompiledScript compiledScript,
                          Script script,
                          OutputStream out);
    void saveScript(Integer identifier, Script script);
    boolean stopScriptExecution(Integer id);
    Script getScript(Integer Id);
    CompiledScript compile(String script) throws ScriptException;
    Collection<Script> getScripts();
}
