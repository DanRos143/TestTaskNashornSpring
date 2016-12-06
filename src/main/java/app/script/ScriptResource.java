package app.script;

import app.view.View;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.hateoas.ResourceSupport;

@JsonPropertyOrder({"id","status", "body","output", "_links"})
public class ScriptResource extends ResourceSupport {

    @JsonView(View.Rest.class)
    @JsonProperty("id")
    private Integer identifier;
    @JsonView(View.Rest.class)
    private ScriptStatus status;
    @JsonView(View.Body.class)
    private String body;
    @JsonView(View.Output.class)
    private StringBuilder output;

    public ScriptResource(){
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
