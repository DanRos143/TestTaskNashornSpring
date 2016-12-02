package app.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import app.compiler.ScriptCompilerImpl;
import app.service.ScriptServiceImpl;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScriptServiceImpl.class, ScriptCompilerImpl.class, ScriptController.class})
public class ControllerTest {

    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void connectionResetByPeerTest() throws Exception {


    }



}
