package rest.compiler;

import javax.script.CompiledScript;
import javax.script.ScriptException;


public interface ScriptCompiler {
    CompiledScript compile(String script)
            throws ScriptException;
}
