package rest.script;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

public class ScriptWrapper extends ResourceSupport {


    private Integer identifier;

    @JsonIgnore
    private String body;

    private ScriptStatus status;

    @JsonIgnore
    private StringBuilder output;


    @JsonIgnore
    private Thread thread;

    public ScriptWrapper(Integer id, String content) {
        this.identifier = id;
        this.body = content;
        this.status = ScriptStatus.Waiting;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public StringBuilder getOutput() {
        return output;
    }

    public void setOutput(StringBuilder output) {
        this.output = output;
    }

    public ScriptStatus getStatus() {
        return status;
    }

    public void setStatus(ScriptStatus status) {
        this.status = status;
    }




}
