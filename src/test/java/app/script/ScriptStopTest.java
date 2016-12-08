package app.script;

import app.compiler.ScriptCompiler;
import app.compiler.ScriptCompilerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptCompilerImpl.class})
public class ScriptStopTest {

    @Autowired
    ScriptCompiler compiler;


    @Test
    public void testStopWithDelay() throws ScriptException, InterruptedException {
        Script script = new Script(1, "while(true) {}");
        CompiledScript compiledScript = compiler.compile(script.getBody());
        Thread t1 = new Thread(() -> {
            try {
                script.setThread(Thread.currentThread());
                compiledScript.eval();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        script.stopScriptExecution();
        Assert.assertTrue(t1.isInterrupted());
    }
}
