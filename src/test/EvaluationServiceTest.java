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


//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {ScriptServiceImpl.class, ScriptCompilerImpl.class, ScriptEvalController.class})
public class EvaluationServiceTest {


    //@Autowired
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


    @Test
    public void nashornExecutionWithBindings() throws InterruptedException {
        ScriptEngine nashorn =  new ScriptEngineManager().getEngineByName("nashorn");
        //Bindings bindings = nashorn.createBindings();

        Thread t1 = new Thread(() -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintWriter printWriter = new PrintWriter(baos, true);
                ScriptContext context = nashorn.getContext();
                context.setWriter(printWriter);
                nashorn.eval("for(var i = 0; i < 10; i++){" +
                        "print(new Date());" +
                        "java.util.concurrent.TimeUnit.SECONDS.sleep(2);" +
                        "}", context);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                ScriptContext context = nashorn.getContext();
                nashorn.eval("for(var i = 0; i < 10; i++){" +
                        "print(new Date());" +
                        "java.util.concurrent.TimeUnit.SECONDS.sleep(2);" +
                        "}", context);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(20);


    }



}
