package app.compiler;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * This interface contains one method - compile, which creates
 * new CompiledScript object from source String
 * @author danros
 * @see CompiledScript
 * @see ScriptException
 *
 */
public interface ScriptCompiler {
    /**
     *
     * @param body <code>String</code> javascript source code to be compiled
     * @return Creates a new <code>CompiledScript</code> by compiling source
     * @throws ScriptException - if source string is not valid javascript code
     */
    CompiledScript compile(String body)
            throws ScriptException;
}
