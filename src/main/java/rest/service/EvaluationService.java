package rest.service;

import org.springframework.http.ResponseEntity;
import rest.script.ScriptWrapper;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by danros on 28.11.16.
 */

public interface EvaluationService {

    Future<?> runAsynchronously(String script, HttpServletResponse response);
    ResponseEntity runSynchronously(String script);
    ConcurrentMap<Integer, ScriptWrapper> getScriptWrappers();
    AtomicInteger getCounter();
    ResponseEntity killScript(Integer id);

}
