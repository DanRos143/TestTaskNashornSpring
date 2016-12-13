package app.script;

import app.controller.ScriptController;
import app.view.View;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.util.concurrent.TimeUnit;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@NoArgsConstructor
@Getter
@Setter
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

    @JsonView(View.Rest.class)
    private long totalTime;

    @JsonView(View.Rest.class)
    private final String units = TimeUnit.MILLISECONDS.name();

    public ScriptResource addLinksByRepresentationType(View.ViewType type){
        switch (type){
            case Rest:
                add(
                        linkTo(methodOn(ScriptController.class).getScriptOutput(identifier))
                                .slash(identifier + "/output").withRel("output"),
                        linkTo(methodOn(ScriptController.class).getScriptBody(identifier))
                                .slash(identifier + "/body").withRel("body")
                );
                if (!status.equals(ScriptStatus.Done) &&
                        !status.equals(ScriptStatus.Error))
                    add(linkTo(methodOn(ScriptController.class).stopScriptExecution(identifier))
                            .slash(identifier).withRel("stop"));
                break;
            case Body:
                getLinks().clear();
                add(linkTo(methodOn(ScriptController.class).getScriptBody(identifier))
                        .slash(identifier)
                        .slash("body")
                        .withSelfRel());
                break;
            case Output:
                getLinks().clear();
                add(linkTo(methodOn(ScriptController.class).getScriptOutput(identifier))
                        .slash(identifier)
                        .slash("output")
                        .withSelfRel());
                break;
        }
        return this;
    }
}
