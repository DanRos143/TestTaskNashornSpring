package rest;

import org.springframework.stereotype.Component;

import javax.script.*;

/**
 * Created by danros on 25.11.16.
 */
@Component
public class ScriptEvaluatorImpl implements ScriptEvaluator{
    private ScriptEngine nashorn;

    public ScriptEvaluatorImpl() {
        nashorn = new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public void evaluate(String script) {

    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        Compilable compilable = (Compilable) nashorn;
         return compilable.compile(script);
    }


}
