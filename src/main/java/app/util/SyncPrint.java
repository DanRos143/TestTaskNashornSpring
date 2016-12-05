package app.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.function.Function;

public class SyncPrint implements Function<Object, Void> {
    private OutputStreamWriter writer;
    private StringBuilder sb;

    public SyncPrint(OutputStream out, StringBuilder sb) {
        this.writer = new OutputStreamWriter(out);
        this.sb = sb;
    }

    @Override
    public Void apply(Object msg) {
        try {
            writer.write(msg.toString() + "\n");
            writer.flush();
            sb.append(msg);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().stop();
        }
        return null;
    }
}