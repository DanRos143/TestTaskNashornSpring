package app.script;

import app.writer.TeeWriter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.StringBuilderWriter;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Identifiable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * Domain model object which contains all related to script data
 * and defines it behavior.
 * Implements Identifiable to be able assembling to resources via ResourceAssembler implementation.
 * Implements StreamingResponseBody for synchronous script execution mode without holding container
 * thread. Stores all script output(including exception messages) to StringBuilder called output;
 * @author danros
 * @see TeeWriter
 * @see StringBuilderWriter
 * @see CompiledScript
 * @see ScriptStatus
 * @see Identifiable
 * @see StreamingResponseBody
 */
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

    public Script(Integer id, String body, CompiledScript compiled){
        this.id = id;
        this.body = body;
        this.output = new StringBuilder();
        this.status = ScriptStatus.Waiting;
        this.compiled = compiled;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        try {
            log.info("headers sent, execution started");
            out.flush();
            thread = Thread.currentThread();
            status = ScriptStatus.Running;
            compiled.eval(createContext(out));
            status = ScriptStatus.Done;
            log.info("execution finished");
        } catch (ScriptException e) {
            out.write(e.getMessage().getBytes());
            output.append(e.getMessage());
            status = ScriptStatus.Error;
            log.error("script exception occurred");
        } finally {
            out.close();
        }
    }

    public void runAsync(){
        try {
            log.info("async execution started");
            thread = Thread.currentThread();
            status = ScriptStatus.Running;
            compiled.eval(createContext(null));
            status = ScriptStatus.Done;
            log.info("async execution finished");
        } catch (ScriptException e) {
            output.append(e.getMessage());
            status = ScriptStatus.Error;
            log.info("exception occurred during evaluation");
        }
    }

    /**
     * shuts CompiledScript evaluation by interrupting the thread, and if it doesn't help - kills it
     * after specified delay with thread.stop(). Delay is configurable in application.yml
     */
    @SneakyThrows(InterruptedException.class)
    public void stopExecution() {
        thread.interrupt();
        TimeUnit.SECONDS.sleep(delay);
        if (thread.isAlive()) thread.stop();
    }

    /**
     * creates ScriptContext object for separated printing with writers for async and sync mode respectively
     * @param out <code>OutputStream</code> object which indicates evaluation mode. If it is null - async mode, else - sync.
     * @return implementing <code>ScriptContext<code/> interface object with writer set.
     */
    private ScriptContext createContext(OutputStream out){
        ScriptContext ctx = new SimpleScriptContext();
        ctx.setWriter(out == null? new StringBuilderWriter(output):
                new TeeWriter(out, output));
        log.info("script context built");
        return ctx;
    }
}