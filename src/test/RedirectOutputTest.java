import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by danros on 28.11.16.
 */
public class RedirectOutputTest {


    @Test
    public void redirectConsoleOutputToString(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream save = System.out;
        StringBuilder sb = new StringBuilder();
        System.setOut(new PrintStream(baos));
        for (int i = 0; i < 10; i++) {
            System.out.println("test" + i);
            sb.append(baos.toString());
        }
        System.setOut(save);
        System.out.println(sb);
    }

}
