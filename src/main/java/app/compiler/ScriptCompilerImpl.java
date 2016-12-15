package app.compiler;

import org.springframework.stereotype.Component;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


@Component
public class ScriptCompilerImpl implements ScriptCompiler {
    private Compilable nashorn;

    public ScriptCompilerImpl() {
        nashorn = (Compilable)
                new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public CompiledScript compile(String body) throws ScriptException {
        return nashorn.compile(body);
    }
}
