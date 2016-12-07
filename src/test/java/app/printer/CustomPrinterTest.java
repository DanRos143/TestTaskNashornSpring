package app.printer;

import app.compiler.ScriptCompiler;
import app.compiler.ScriptCompilerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.xml.ws.Response;
import java.io.ByteArrayOutputStream;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ScriptCompilerImpl.class})
public class CustomPrinterTest {

    @Autowired
    ScriptCompiler compiler;

    @Test
    public void testJSPrintSyncModeOverride() throws InterruptedException, ScriptException {
        String body = "for(var i = 0; i<5;i++) print(i);";
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        Bindings b1 = compiler.createBindings();
        Bindings b2 = compiler.createBindings();
        b1.put("print", new SyncPrint(baos1, sb1));
        b2.put("print", new SyncPrint(baos2, sb2));
        compiler.compile(body).eval(b1);
        compiler.compile(body).eval(b2);
        Assert.assertTrue(baos1.toString().equals(baos2.toString()));
    }

    @Test(expected = ScriptException.class)
    public void blockJSPrintFunctionTest() throws ScriptException {
        String invalidScript = "print(new Date())";
        Bindings bindings = compiler.createBindings();
        bindings.remove("print");
        compiler.compile(invalidScript).eval(bindings);
    }
}
