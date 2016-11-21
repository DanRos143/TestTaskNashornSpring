package rest;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by danros on 21.11.16.
 */

public class ScriptEvaluator {
    private final Lock lock = new ReentrantLock();

    public String evaluate(String script){
        String response = null;
        ScriptEngineManager factory =
                new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");
        try {
            lock.lock();
            response = (String) engine.eval(script);
        } catch (ScriptException se){
            se.printStackTrace();
        } finally {
            lock.unlock();
        }
        return response;
    }
}
