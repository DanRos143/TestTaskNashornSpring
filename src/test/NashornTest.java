import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.script.*;
import java.sql.Time;
import java.util.concurrent.*;


public class NashornTest {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");

    @Test
    public void scriptExecutionCancelingTest() throws InterruptedException {

        Future<?> submit = executorService.submit(() -> {
            try {
                nashorn.eval("while(true){ var d = new Date();print(d);}");
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            return new ResponseEntity(HttpStatus.CREATED);
        });
        TimeUnit.SECONDS.sleep(2);
        submit.cancel(true);
        Assert.assertTrue(submit.isDone());
    }



}

