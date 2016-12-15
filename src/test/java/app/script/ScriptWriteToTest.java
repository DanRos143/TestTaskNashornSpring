package app.script;

import app.compiler.ScriptCompiler;
import app.compiler.ScriptCompilerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptCompilerImpl.class})
public class ScriptWriteToTest {


    @Autowired
    private ScriptCompiler compiler;


    @Test
    public void writeToTest() throws IOException, ScriptException {
        String source = "print('greetings')";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Script script = new Script(1, "print();", compiler.compile(source));
        script.writeTo(baos);
        Assert.assertTrue(!baos.toString().isEmpty());
    }

    @Test
    public void stopExecTest() throws ScriptException, InterruptedException {
        Script script = new Script(0, "1", compiler.compile("1"));
        Thread t = new Thread(script::eval);
        t.setName("custom-thread");
        t.start();
        t.join();
        Assert.assertNotNull(script.getThread());
        Assert.assertEquals("custom-thread", t.getName());
    }
}
