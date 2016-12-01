package rest.compiler;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rest.controller.ScriptEvalController;
import rest.service.ScriptServiceImpl;

import javax.script.Compilable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScriptCompilerImpl.class)
public class CompilerTest {


    @Autowired
    private ScriptCompiler scriptCompiler;

    @Test
    public void runtimeExceptionAppearenceTest(){
        String notAValidScript = "print(";
        Compilable nashorn = (Compilable) new ScriptEngineManager().getEngineByName("nashorn");
        try {
            nashorn.compile(notAValidScript);
        } catch (ScriptException sc) {
            System.out.println(sc.getMessage());
        }
    }


    @Test
    public void getExceptionViaCompiler(){
        String notAValidScript = "print(";
        try {
            scriptCompiler.compile(notAValidScript);
        } catch (ScriptException e) {
            System.out.println(e.getMessage());
        }
    }
}
