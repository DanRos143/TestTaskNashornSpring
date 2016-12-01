package rest.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import rest.script.ScriptWrapper;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.Future;


public interface ScriptService {
    void runAsynchronously(String script, ResponseBodyEmitter emitter)
            throws IOException, ScriptException;

    void runSynchronously(String script, PrintWriter printWriter)
            throws IOException, ScriptException;

    ResponseEntity stopScriptExecution(Integer id);

    Set<String> getLinks(String path);
    ScriptWrapper getScriptInfo(Integer scriptId);
}
