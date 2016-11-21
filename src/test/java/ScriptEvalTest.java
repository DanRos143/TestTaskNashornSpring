import org.junit.Test;
import rest.ScriptEvaluator;

/**
 * Created by danros on 21.11.16.
 */

public class ScriptEvalTest {

    @Test
    public void scriptExecutionTest(){
        ScriptEvaluator evaluator =
                new ScriptEvaluator();
        evaluator.evaluate("print('Test completed!')");
    }


}
