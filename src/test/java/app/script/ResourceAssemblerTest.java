package app.script;

import app.compiler.ScriptCompilerImpl;
import app.controller.ScriptController;
import app.service.ScriptServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptController.class, ScriptServiceImpl.class, ScriptCompilerImpl.class})
public class ResourceAssemblerTest {
    private ScriptResourceAssembler assembler = new ScriptResourceAssembler();


    @Test
    public void resourceIdLinkTest(){
        Script script = new Script(1, "print('greetings')");
        ScriptResource scriptResource = assembler.toResource(script);
        Link selfLink = new Link("http://localhost/api/scripts/1");
        Assert.assertThat(scriptResource.getId(), is(selfLink));
        Assert.assertThat(scriptResource.getId().getRel(), is(Link.REL_SELF));
    }
}
