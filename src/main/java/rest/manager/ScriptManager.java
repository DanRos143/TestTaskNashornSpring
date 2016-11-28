package rest.manager;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * Created by danros on 25.11.16.
 */
public interface ScriptManager {
    void evaluate(String script) throws ScriptException;
    CompiledScript compile(String script) throws ScriptException;
}
