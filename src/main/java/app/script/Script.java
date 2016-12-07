package app.script;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Identifiable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
public class Script implements Identifiable<Integer>, StreamingResponseBody {
    private Integer id;
    private ScriptStatus status;
    private String body;
    private StringBuilder output;
    private long executionTime;
    private Thread thread;

    public Script(Integer id, String body) {
        this.id = id;
        this.body = body;
        this.output = new StringBuilder();
        this.status = ScriptStatus.Waiting;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {

    }

    public void stopScriptExecution(){//hardcoded sleep, should be replaced with application.properties constant
        try {
            thread.interrupt();
            TimeUnit.SECONDS.sleep(2);
            if (thread.isAlive()) thread.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
