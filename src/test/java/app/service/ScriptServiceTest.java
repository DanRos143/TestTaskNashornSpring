package app.service;


import app.Application;
import app.compiler.ScriptCompilerImpl;
import app.script.Script;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.ScriptException;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptCompilerImpl.class, ScriptServiceImpl.class, Application.class})
public class ScriptServiceTest {

    @Autowired
    private ScriptService service;

    @Value("${application.asyncExecutor.threadNamePrefix}")
    private String threadNamePrefix;
    @Value("${application.script.stopDelay}")
    private long delay;


    @Test(expected = ScriptException.class)
    public void saveUncompilableScriptTest() throws ScriptException {
        String invalid = "print(";
        service.compileAndSave(invalid);
        Assert.assertNull(service.getScripts());
    }

    @Test
    public void saveCompilableTest() throws ScriptException {
        String valid = "print();";
        Script script = service.compileAndSave(valid);
        Assert.assertNotNull(script);
    }

    @Test
    public void submitAsyncTest() throws ScriptException, InterruptedException {
        String source = "print(0)";
        Script script = service.compileAndSave(source);
        service.submitAsync(script);
        TimeUnit.SECONDS.sleep(delay);
        Assert.assertTrue(script.getThread().getName().contains(threadNamePrefix));
    }

    @Test(expected = NullPointerException.class)
    public void produceNullPointerExceptionByGettingNonExistingScript() {
        Integer notExistingKey = Integer.MAX_VALUE;
        Assert.assertTrue(service.getScripts().size() < notExistingKey);
        Script script = service.getScript(notExistingKey);
        Assert.assertTrue(notExistingKey.equals(script.getId()));
    }
}
