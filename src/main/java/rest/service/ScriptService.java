package rest.service;

import org.springframework.http.ResponseEntity;
import rest.script.ScriptWrapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by danros on 28.11.16.
 */

public interface ScriptService {

    Future<?> runAsynchronously(String script, HttpServletResponse response) throws IOException;
    void runSynchronously(String script, HttpServletResponse response) throws IOException;
    ConcurrentMap<Integer, ScriptWrapper> getScriptWrappers();
    ResponseEntity stopScriptExecution(Integer id);
    Iterable<ScriptWrapper> getLinks(String path);

}
