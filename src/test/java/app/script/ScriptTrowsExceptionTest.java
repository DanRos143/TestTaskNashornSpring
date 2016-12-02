package app.script;

import app.compiler.ScriptCompiler;
import app.compiler.ScriptCompilerImpl;
import app.util.SyncPrint;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.*;
import java.io.ByteArrayOutputStream;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptCompilerImpl.class})
public class ScriptTrowsExceptionTest {


    @Autowired
    ScriptCompiler compiler;

    @Test
    public void throwJavaExceptionFromJS() throws ScriptException, InterruptedException {
        String script = "for(var i = 0; i< 5; i++){print('hi')}";
        ScriptContext ctx = new SimpleScriptContext();
        Bindings bindings = new SimpleBindings();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();
        System.out.println(bindings.get("print"));//returns null
        bindings.forEach((s, o) -> System.out.println(s + ":" + o));
        //bindings.put("print", new SyncPrint(baos, sb));
        ctx.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        compiler.compile(script).eval(ctx);
        //System.out.println(sb);

    }

    @Test
    public void getBindingsTest() throws ScriptException {
        StringBuilder sb = new StringBuilder();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NashornScriptEngine nashorn = (NashornScriptEngine)
                new ScriptEngineManager().getEngineByName("nashorn");
        Bindings bindings = nashorn.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.remove("print");
        ScriptContext ctx = new SimpleScriptContext();
        ctx.setBindings(nashorn.getBindings(ScriptContext.ENGINE_SCOPE), ScriptContext.ENGINE_SCOPE);
        ctx.getBindings(ScriptContext.ENGINE_SCOPE).put("print", new SyncPrint(baos, sb));
        nashorn.eval("print(4)", ctx);
    }

}
