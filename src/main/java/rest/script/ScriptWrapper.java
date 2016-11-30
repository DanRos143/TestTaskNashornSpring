package rest.script;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.concurrent.Future;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScriptWrapper {
    @JsonIgnore
    private String content;
    private ScriptStatus status;

    @JsonIgnore
    private Thread thread;

    public ScriptWrapper(String content) {
        this.content = content;
        this.status = ScriptStatus.Waiting;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ScriptStatus getStatus() {
        return status;
    }

    public void setStatus(ScriptStatus status) {
        this.status = status;
    }

}
