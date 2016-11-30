package rest.service;

import org.springframework.http.ResponseEntity;
import rest.script.ScriptWrapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Future;


public interface ScriptService {
    Future<?> runAsynchronously(String script, HttpServletResponse response)
            throws IOException;

    void runSynchronously(String script, HttpServletResponse response)
            throws IOException;

    ResponseEntity stopScriptExecution(Integer id);

    Set<String> getLinks(String path);
    ScriptWrapper getScriptInfo(Integer scriptId);
}