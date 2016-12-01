package rest.compiler;

import org.springframework.stereotype.Component;

import javax.script.*;


@Component
public class ScriptCompilerImpl implements ScriptCompiler {
    private Compilable nashorn;

    public ScriptCompilerImpl() {
        nashorn = (Compilable) new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return nashorn.compile(script);
    }
}
