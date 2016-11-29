package rest.compiler;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.io.PrintWriter;


public interface ScriptCompiler {
    CompiledScript compile(String script)
            throws ScriptException;
}
