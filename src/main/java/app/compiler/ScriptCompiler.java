package app.compiler;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;


public interface ScriptCompiler {
    CompiledScript compile(String script)
            throws ScriptException;
    Bindings createBindings();
}
