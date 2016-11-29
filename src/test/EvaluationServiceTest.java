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
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * Created by danros on 28.11.16.
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptServiceImpl.class, ScriptCompilerImpl.class, ScriptEvalController.class})
public class EvaluationServiceTest {


    @Autowired
    ScriptCompiler scriptManager;

    @Test
    public void uselessTest(){
        final ScriptEngineManager mgr = new ScriptEngineManager();
        for(ScriptEngineFactory fac: mgr.getEngineFactories()) {
            System.out.println(String.format("%s (%s), %s (%s), %s", fac.getEngineName(),
                    fac.getEngineVersion(), fac.getLanguageName(),
                    fac.getLanguageVersion(), fac.getParameter("THREADING")));
        }
    }


    @Test(expected = ScriptException.class)
    public void nashornExecutionWithBindings() throws InterruptedException, ScriptException {
        String badScript = "print(1";
        scriptManager.compile(badScript);
    }



}
