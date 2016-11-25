package rest;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * Created by danros on 25.11.16.
 */
public interface ScriptEvaluator {
    void evaluate(String script);
    CompiledScript compile(String script) throws ScriptException;
}
