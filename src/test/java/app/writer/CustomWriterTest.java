package app.writer;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CustomWriterTest {

    @Test
    public void testTeeWriter() throws IOException {
        StringBuilder output = new StringBuilder();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TeeWriter writer = new TeeWriter(baos, output);
        writer.write("just an example");
        System.out.println(output.toString());
        Assert.assertTrue(!output.toString().isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testBehaviorWithNullWriter() throws IOException {
        TeeWriter writer = new TeeWriter(null, new StringBuilder());
        writer.write("greetings");
    }

}
