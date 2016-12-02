package app.script;

import app.view.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ScriptWrapper extends ResourceSupport {
    @JsonView(View.Rest.class)
    private Integer identifier;
    @JsonView(View.Rest.class)
    private ScriptStatus status;
    @JsonView(View.Body.class)
    private String body;
    @JsonView(View.Output.class)
    private StringBuilder output;

    @JsonIgnore
    private Thread thread;

    public ScriptWrapper(Integer id, String content) {
        this.identifier = id;
        this.body = content;
        this.output = new StringBuilder();
        this.status = ScriptStatus.Waiting;
    }

    public Integer getIdentifier() {
        return this.identifier;
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

    @Override
    @JsonView(View.Rest.class)
    public List<Link> getLinks() {
        return super.getLinks();
    }
}
