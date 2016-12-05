package app.script;

import app.view.View;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;


public class ScriptResource extends Resource<Script> {

    @JsonProperty("id")
    @JsonView(View.Rest.class)
    private Integer identifier;
    @JsonView(View.Rest.class)
    private ScriptStatus status;
    @JsonView(View.Body.class)
    private String body;
    @JsonView(View.Output.class)
    private StringBuilder output;

    public ScriptResource(){
        super(new Script());
    }

    public ScriptResource(Script content, Link... links) {
        super(content, links);
    }

    public ScriptResource(Script content, Iterable<Link> links) {
        super(content, links);
    }

    public String getBody() {
        return body;
    }

    public Integer getIdentifier(){
        return identifier;
    }


    public void setIdentifier(Integer id) {
        this.identifier = id;
    }

    public ScriptStatus getStatus() {
        return status;
    }

    public void setStatus(ScriptStatus status) {
        this.status = status;
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
}
