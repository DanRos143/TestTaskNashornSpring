package rest;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.Future;

/**
 * Created by danros on 21.11.16.
 */
public class ScriptWrapper {
    private String content;

    @JsonIgnore
    private StringBuilder output;
    private Status status;


    private Future<String> future;


    public ScriptWrapper(String content) {
        this.content = content;
        this.status = Status.Waiting;
        this.output = new StringBuilder();
    }
    public Future<String> getFuture() {
        return future;
    }

    public void setFuture(Future<String> future) {
        this.future = future;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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