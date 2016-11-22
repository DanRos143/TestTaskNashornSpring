package rest;

import javax.script.*;
import java.util.concurrent.TimeUnit;
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
            System.out.println("evaluating");
            response = (String) engine.eval(script);
            System.out.println("finished");
            if (response == null) response = "";
        } catch (ScriptException se){
            response = se.getMessage();
        } finally {
            lock.unlock();
        }
        return response;
    }

}
