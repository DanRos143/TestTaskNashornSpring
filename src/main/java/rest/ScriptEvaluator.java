package rest;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedOutputStream;
import java.util.concurrent.locks.Lock;

/**
 * Created by danros on 23.11.16.
 */
public class ScriptEvaluator {

    public String evaluate(String script){
        String response = "";
        ScriptEngineManager factory =
                new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");
        try {
            response = (String) engine.eval(script);
            if (response == null) response = "";
        } catch (ScriptException se){
            response = se.getMessage();
        }
        return response;
    }

}
