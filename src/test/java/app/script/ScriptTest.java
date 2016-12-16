package app.script;

import app.writer.TeeWriter;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScriptTest {

    private Method method;
    private Script script;

    @Before
    public void setup() throws NoSuchMethodException {
        script = new Script(1,null,null);
        method = Script.class.getDeclaredMethod("createContext", OutputStream.class);
        method.setAccessible(true);
    }

    @Test
    public void testCreateContextForAsyncMode()
            throws InvocationTargetException, IllegalAccessException {
        OutputStream out = null;
        ScriptContext context = (ScriptContext)
                ReflectionUtils.invokeMethod(method, script, out);
        Assert.assertTrue(context.getWriter() instanceof StringBuilderWriter);
    }
    @Test
    public void createContextForSyncMode() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ScriptContext context = (ScriptContext)
                ReflectionUtils.invokeMethod(method, script, baos);
        Assert.assertTrue(context.getWriter() instanceof TeeWriter);
    }

    @Test
    public void handleExceptionTest() throws NoSuchMethodException {
        Method handle = Script.class.getDeclaredMethod("handleException", ScriptException.class);
        handle.setAccessible(true);
        ReflectionUtils.invokeMethod(handle, script, new ScriptException("invalid script"));
        Assert.assertEquals(script.getStatus(), Status.Broken);
    }

}
