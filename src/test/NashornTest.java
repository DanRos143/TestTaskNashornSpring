import org.junit.Assert;
import org.junit.Test;

import javax.script.*;

/**
 * Created by danros on 25.11.16.
 */
public class NashornTest {




    @Test(expected = ScriptException.class)
    public void preCompileFailTest() throws ScriptException {
        String notValidScript = "print(";
        ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
        Compilable compilable = (Compilable) nashorn;
        CompiledScript compiledScript = compilable.compile(notValidScript);
    }

}
