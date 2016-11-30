package rest.compiler;

import org.springframework.stereotype.Component;

import javax.script.*;


@Component
public class ScriptCompilerImpl implements ScriptCompiler {
    private ScriptEngine nashorn;

    public ScriptCompilerImpl() {
        nashorn = new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        Compilable compilable = (Compilable) nashorn;
        return compilable.compile(script);
    }
}
