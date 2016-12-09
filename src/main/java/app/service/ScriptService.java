package app.service;

import app.script.Script;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;


public interface ScriptService {
    Script getScript(Integer id);
    CompiledScript compile(String script) throws ScriptException;
    Collection<Script> getScripts();
    void submitAsync(Script script);
    void saveScript(Script script);
}
