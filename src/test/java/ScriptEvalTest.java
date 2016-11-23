import org.junit.Test;

import java.util.concurrent.*;

/**
 * Created by danros on 21.11.16.
 */

public class ScriptEvalTest {



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
