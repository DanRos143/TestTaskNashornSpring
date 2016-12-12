package app.compiler;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * @author danros
 *
 */
public interface ScriptCompiler {
    CompiledScript compile(String script)
            throws ScriptException;
}
