import org.junit.Test;
import rest.ScriptEvaluator;
import sun.nio.ch.ThreadPool;

import java.util.concurrent.*;

/**
 * Created by danros on 21.11.16.
 */

public class ScriptEvalTest {

    @Test
    public void scriptExecutionTest(){
        ScriptEvaluator evaluator =
                new ScriptEvaluator();
        System.out.println(System.currentTimeMillis());
        evaluator.evaluate("" +
                "for(var i = 0; i < 10; i++) print('Test completed!')");
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void futureExecutionTest() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();
        Callable<String> callable = () -> {
            TimeUnit.SECONDS.sleep(1);
            return "from test";
        };
        Future<String> future = pool.submit(callable);

        while (!future.isDone()) System.out.println(future.isDone());

    }
    @Test
    public void threadPoolTest(){

    }


}
