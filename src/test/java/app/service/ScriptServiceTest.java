package app.service;


import app.compiler.ScriptCompilerImpl;
import app.script.Script;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = {ScriptServiceImpl.class, ScriptCompilerImpl.class})
public class ScriptServiceTest {

    //@Autowired
    ScriptService service;

/*    @Test(expected = ThreadDeath.class)
    public void syncScriptStopTest() throws ScriptException, IOException {
        String body = "while(true) print('test passed')";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompiledScript compiledScript = service.compile(body);
        Script script = new Script(1, body);
        Thread killer = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                script.getThread().stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        killer.start();
        service.runSync(compiledScript, script, baos);
        Assert.assertTrue(!script.getThread().isAlive());
    }*/
}
