package rest.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import rest.script.ScriptWrapper;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by danros on 28.11.16.
 */

public interface EvaluationService {

    DeferredResult<ResponseEntity> runAsynchronously(String script);
    ResponseEntity runSynchronously(String script);
    ConcurrentMap<Integer, ScriptWrapper> getScriptWrappers();
    AtomicInteger getCounter();
    ResponseEntity killScript(Integer id);

}
