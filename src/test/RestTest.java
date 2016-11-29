import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import rest.controller.ScriptEvalController;
import rest.compiler.ScriptCompilerImpl;
import rest.service.ScriptServiceImpl;
import javax.script.ScriptException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Created by danros on 25.11.16.
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptServiceImpl.class, ScriptCompilerImpl.class, ScriptEvalController.class})
public class RestTest {

    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }


    @Test
    public void evalScriptTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/api/scripts/")
                .content("print('test passed!')"))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        System.out.println(response.getHeaderValue("Location"));
    }



}
