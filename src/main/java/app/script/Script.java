package app.script;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Identifiable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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

    @Value("${application.script.stopDelay}")
    private long delay;

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
        //what should i write here? this is not the right way to do sync, or i'm missing something?

    }

    @SneakyThrows(InterruptedException.class)
    public void stopScriptExecution() {
        thread.interrupt();
        TimeUnit.SECONDS.sleep(delay);
        if (thread.isAlive()) thread.stop();
    }
}
