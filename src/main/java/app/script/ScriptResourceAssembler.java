package app.script;

import app.controller.ScriptController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ScriptResourceAssembler extends ResourceAssemblerSupport<Script, ScriptResource> {

    public ScriptResourceAssembler() {
        super(ScriptController.class, ScriptResource.class);
    }

    @Override
    public ScriptResource toResource(Script script) {
        ScriptResource resource = createResourceWithId(script.getId(), script);
        resource.setIdentifier(script.getId());
        resource.setStatus(script.getStatus());
        resource.setBody(script.getBody());
        resource.setOutput(script.getOutput());
        resource.setTotalTime(script.getExecutionTime());
        return resource;
    }
}
