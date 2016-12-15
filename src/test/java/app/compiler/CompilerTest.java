package app.compiler;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScriptCompilerImpl.class)
public class CompilerTest {


    @Autowired
    private ScriptCompiler compiler;


    @Test(expected = ScriptException.class)
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
    }

    @Test
    public void throwIOExceptionFromScript() {
        Throwable t = null;
        String script = "print('preparing to throw');throw new java.io.IOException();";
        try {
            compiler.compile(script).eval();
        } catch (ScriptException e) {
            t = e.getCause();
        }
        Assert.assertTrue(t.getCause() instanceof IOException);
    }

}
