package app.script;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptResourceAssembler.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScriptResourceTest {
    private final String[] possibleRelations = {"output", "body", "stop"};

    @Autowired
    private ResourceAssemblerSupport<Script, ScriptResource> assembler;

    private Script script;
    private ScriptResource resource;


    @Before
    public void setup() {
        script = new Script(0, null, null);
        resource = assembler.toResource(script);
    }

    @Test
    public void addStopDependingOnStatusRelTest() {
        resource.buildLinks();
        Assert.assertNotNull(resource.getLink(possibleRelations[2]));
    }

    @Test
    public void linksDoesNotContainStopRel() {
        script.setStatus(Status.Done);
        resource = assembler.toResource(script);
        resource.buildLinks();
        Assert.assertNull(resource.getLink(possibleRelations[2]));
    }

    @Test
    public void statusBrokenDoesNotProduceStopRelTest() {
        script.setStatus(Status.Broken);
        resource = assembler.toResource(script);
        resource.buildLinks();
        Assert.assertNull(resource.getLink(possibleRelations[2]));
    }

}
