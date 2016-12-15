package app.script;

import app.util.Timer;
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
    private Thread thread;
    private CompiledScript compiled;
    private String executionTime;
    private Timer timer;
    @Value("${application.script.stopDelay}")
    private long delay;

    public Script(Integer id, String body, CompiledScript compiled){
        this.id = id;
        this.body = body;
        this.compiled = compiled;
        this.output = new StringBuilder();
        this.status = ScriptStatus.Waiting;
        this.timer = new Timer();
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        try {
            out.flush();
            log.info("headers sent, execution started");
            setThreadAndEval(out);
        } catch (ScriptException e) {
            out.write(e.getMessage().getBytes());
            handleException(e);
        } finally {
            executionTime = timer.stop();
            log.info("closing connection...");
            if (status.equals(ScriptStatus.Running)) status = ScriptStatus.Error;
            out.close();
        }
    }

    /**
     * This method is submitted by executor.
     * Evaluates CompiledScript and manages script status
     */
    public void eval(){
        try {
            log.info("async execution started");
            setThreadAndEval(null);
            log.info("async execution finished");
        } catch (ScriptException e) {
            handleException(e);
        }
    }

    /**
     * shuts CompiledScript evaluation by interrupting the thread, and if it doesn't help - kills it
     * after specified delay with thread.stop(). Delay is configurable in application.yml
     */
    @SuppressWarnings("deprecation")
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

    private void handleException(ScriptException e) {
        executionTime = timer.stop();
        output.append(e.getMessage());
        status = ScriptStatus.Error;
        log.error("script exception occurred");
    }

    private void setThreadAndEval(OutputStream out) throws ScriptException {
        thread = Thread.currentThread();
        status = ScriptStatus.Running;
        timer.start();
        compiled.eval(createContext(out));
        status = ScriptStatus.Done;
        log.info("execution finished");
    }
}