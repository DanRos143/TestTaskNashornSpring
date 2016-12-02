package app.compiler;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.stereotype.Component;

import javax.script.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.function.Function;


@Component
public class ScriptCompilerImpl implements ScriptCompiler {
    private NashornScriptEngine nashorn;

    public ScriptCompilerImpl() {
        nashorn = (NashornScriptEngine)
                new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return nashorn.compile(script);
    }

    @Override
    public Bindings getBindings() {
        Bindings bindings = nashorn.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.remove("print");
        return nashorn.getBindings(ScriptContext.ENGINE_SCOPE);
    }
}
