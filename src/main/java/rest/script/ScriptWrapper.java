package rest.script;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.Future;

/**
 * Created by danros on 21.11.16.
 */
public class ScriptWrapper {
    private String content;

    @JsonIgnore
    private StringBuilder output;
    private ScriptStatus status;


    private Thread thread;


    public ScriptWrapper(String content, Thread thread) {
        this.content = content;
        this.thread = thread;
        this.status = ScriptStatus.Waiting;
        this.output = new StringBuilder();
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

    public StringBuilder getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = new StringBuilder(output);
    }

    public ScriptStatus getStatus() {
        return status;
    }

    public void setStatus(ScriptStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "content='" + content + '\'' +
                ", status=" + status;
    }
}

/*

 */