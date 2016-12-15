package app.writer;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Custom Writer implementation which simply copy all output into StringBuilder object.
 * Unfortunately apache commons.io contains only TeeOutputStream which is not acceptable
 * in this case.
 */
@Getter
@Setter
public class TeeWriter extends OutputStreamWriter {
    private StringBuilder output;

    public TeeWriter(OutputStream out, StringBuilder output){
        super(out);
        this.output = output;
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        output.append(str);
        super.write(str, off, len);
    }
}
