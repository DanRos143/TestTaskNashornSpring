package rest.manager;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * Created by danros on 25.11.16.
 */
public interface ScriptManager {
    CompiledScript compile(String script) throws ScriptException;
}
