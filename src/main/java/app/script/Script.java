package app.script;

import app.writer.TeeWriter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.StringBuilderWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Identifiable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
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
    private CompiledScript compiled;

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
    public void writeTo(OutputStream out) throws IOException {
        try {
            thread = Thread.currentThread();
            out.flush();
            log.info("headers sent, execution started in {}", thread.getName());
            compiled.eval(createContext(out));
        } catch (ScriptException e) {
            log.error("client disconnected from thread {}", thread.getName());
            stopExecution();
        } catch (IOException ioe){
            log.error("IOException occurred", ioe.getMessage());
        }
    }

    public void runAsync(){
        try {
            thread = Thread.currentThread();
            log.info("sync execution started in {}", thread.getName());
            compiled.eval(createContext(null));
        } catch (ScriptException e) {
            stopExecution();
        }
    }

    @SneakyThrows(InterruptedException.class)
    public void stopExecution() {
        thread.interrupt();
        TimeUnit.SECONDS.sleep(delay);
        if (thread.isAlive()) thread.stop();
    }
    private ScriptContext createContext(OutputStream out){
        ScriptContext ctx = new SimpleScriptContext();
        if (out != null) ctx.setWriter(new TeeWriter(out, output));
        else ctx.setWriter(new StringBuilderWriter(output));
        return ctx;
    }
}