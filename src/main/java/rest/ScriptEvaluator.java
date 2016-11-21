package rest;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


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
