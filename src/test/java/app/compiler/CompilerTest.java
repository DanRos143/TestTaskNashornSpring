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
import java.time.Duration;
import java.time.Instant;


@RunWith(SpringJUnit4ClassRunner.class)
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
}
