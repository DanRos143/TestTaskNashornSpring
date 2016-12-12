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
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptCompilerImpl.class})
public class ScriptStopTest {

    @Autowired
    ScriptCompiler compiler;


    @Test
    public void testStopWithDelay() throws ScriptException, InterruptedException {
        Script script = new Script(1, "while(true) {}", null);
        script.setCompiled(compiler.compile(script.getBody()));
        Thread t1 = new Thread(() -> {
            try {
                script.setThread(Thread.currentThread());
                script.getCompiled().eval();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        TimeUnit.SECONDS.sleep(5);
        script.stopExecution();
        Assert.assertTrue(t1.isInterrupted());
        TimeUnit.SECONDS.sleep(5);
    }
    /*@Test(expected = ScriptException.class)
    public void throwIOExceptionFromScript() {
        Throwable t = null;
        String script = "print('preparing to throw');throw new java.io.IOException();";
        try {
            compiler.compile(script).eval();
        } catch (ScriptException e) {
            t = e.getCause();
            Assert.assertTrue(e.getCause() instanceof IOException);
            System.out.println(e.getCause().getMessage() + " message");
        }
        Assert.assertTrue(t instanceof IOException);
    }*/
}
