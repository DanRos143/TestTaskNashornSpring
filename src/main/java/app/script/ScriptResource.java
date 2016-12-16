package app.script;

import app.controller.ScriptController;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"id","status", "body","output", "_links"})
public class ScriptResource extends ResourceSupport {

    @JsonProperty("id")
    private Integer identifier;
    private Status status;
    private String totalTime;

    public ScriptResource buildLinks() {
        this.add(
                linkTo(methodOn(ScriptController.class).getScriptOutput(identifier))
                        .slash(identifier + "/output").withRel("output"),
                linkTo(methodOn(ScriptController.class).getScriptBody(identifier))
                        .slash(identifier + "/body").withRel("body")
        );
        if (!status.equals(Status.Done) &&
                !status.equals(Status.Broken))
            this.add(linkTo(methodOn(ScriptController.class).stopScriptExecution(identifier))
                    .slash(identifier).withRel("stop"));
        return this;
    }
}
