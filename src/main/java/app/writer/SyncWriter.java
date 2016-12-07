package app.writer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.*;

@Getter
@Setter
public class SyncWriter extends OutputStreamWriter {
    private StringBuilder output;


    public SyncWriter(OutputStream out) {
        super(out);
    }

    public SyncWriter(OutputStream out, StringBuilder output){
        super(out);
        this.output = output;
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        output.append(str);
        super.write(str, off, len);
    }
}
