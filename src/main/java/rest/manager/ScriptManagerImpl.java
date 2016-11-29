package rest.manager;

import org.springframework.stereotype.Component;

import javax.script.*;

/**
 * Created by danros on 25.11.16.
 */
@Component
public class ScriptManagerImpl implements ScriptManager {
    private ScriptEngine nashorn;

    public ScriptManagerImpl() {
        nashorn = new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        Compilable compilable = (Compilable) nashorn;
        return compilable.compile(script);
    }


}
