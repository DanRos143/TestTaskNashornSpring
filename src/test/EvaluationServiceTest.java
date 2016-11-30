import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rest.controller.ScriptEvalController;
import rest.compiler.ScriptCompiler;
import rest.compiler.ScriptCompilerImpl;
import rest.service.ScriptServiceImpl;

import javax.script.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptServiceImpl.class, ScriptCompilerImpl.class, ScriptEvalController.class})
public class EvaluationServiceTest {


    @Autowired
    ScriptCompiler scriptCompiler;

    @Test(expected = ScriptException.class)
    public void nashornExecutionWithBindings() throws InterruptedException, ScriptException {
        String badScript = "print(1";
        scriptCompiler.compile(badScript);
    }


    @Test
    public void threadSafetyTest() throws InterruptedException {
        String script = "for(var i = 0; i < 5; i++) print(i)";
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        PrintWriter writer1 = new PrintWriter(baos1, true);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        PrintWriter writer2 = new PrintWriter(baos2, true);

        ScriptContext sc1 = new SimpleScriptContext();
        sc1.setWriter(writer1);

        ScriptContext sc2 = new SimpleScriptContext();
        sc2.setWriter(writer2);

        Thread t1 = new Thread(() -> {
            try {
                scriptCompiler.compile(script).eval(sc2);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                scriptCompiler.compile(script).eval(sc1);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(2);
        Assert.assertEquals(baos1.toString(), baos2.toString());
    }





}
