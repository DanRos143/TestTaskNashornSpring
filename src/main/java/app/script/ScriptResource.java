package app.script;

import app.view.View;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.util.concurrent.TimeUnit;

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
}
