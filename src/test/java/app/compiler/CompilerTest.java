package app.compiler;


import app.script.Script;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ScriptCompilerImpl.class)
public class CompilerTest {


    @Autowired
    private ScriptCompiler compiler;


    /*@Test(expected = ScriptException.class)
    public void getCompilationException() throws ScriptException {
        String invalidScript = "print(";
        compiler.compile(invalidScript);
    }

    @Test(expected = ScriptException.class)
    public void getRuntimeScriptException() throws ScriptException {
        String validForCompilation = "log(5)";
        String compileError = null;
        CompiledScript compiledScript = null;
        try {
            compiledScript = compiler.compile(validForCompilation);
        } catch (ScriptException e) {
            e.printStackTrace();
            compileError = e.getMessage();
        }
        compiledScript.eval();
        Assert.assertEquals(compileError, null);
    }*/

    @Test
    public void printInSeparatedOut() throws ScriptException, InterruptedException {
        String script = "print(0)";
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        ScriptContext sc1 = new SimpleScriptContext();
        sc1.setWriter(new PrintWriter(baos1, true));
        ScriptContext sc2 = new SimpleScriptContext();
        sc2.setWriter(new PrintWriter(baos2, true));
        CompiledScript compile = compiler.compile(script);
        Thread t1 = new Thread(() -> {
            try {
                compile.eval(sc1);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                compile.eval(sc2);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
        t2.join();
        t2.join();
        System.out.println(baos1.toString());
        System.out.println(baos2.toString());
        Assert.assertTrue(baos1.toString().equals(baos2.toString()));
    }
}
