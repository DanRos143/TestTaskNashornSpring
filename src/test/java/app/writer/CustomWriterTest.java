package app.writer;

import app.compiler.ScriptCompiler;
import app.compiler.ScriptCompilerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ScriptCompilerImpl.class)
public class CustomWriterTest {

    @Autowired
    ScriptCompiler compiler;

    @Test
    public void testCustomWriterWithNashorn() throws ScriptException, IOException {
        String script = "for(var i = 0; i < 5; i++){ print('greetings')}";
        StringBuilder output = new StringBuilder();
        ByteArrayOutputStream outer = new ByteArrayOutputStream();
        TeeWriter writer = new TeeWriter(outer, output);
        ScriptContext ctx = new SimpleScriptContext();
        ctx.setWriter(writer);
        compiler.compile(script).eval(ctx);
        System.out.println(output);
        Assert.assertTrue(!output.toString().isEmpty());
    }

}
