package app.controller;

import app.Application;
import app.compiler.ScriptCompiler;
import app.compiler.ScriptCompilerImpl;
import app.service.ScriptServiceImpl;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.script.ScriptException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ScriptServiceImpl.class, ScriptCompilerImpl.class,
        ScriptController.class, Application.class, GlobalExceptionHandler.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerTest {

    private final String URL = "/api/scripts";
    private MockMvc mockMvc;

    @Autowired
    ScriptCompiler compiler;

    @Autowired
    WebApplicationContext wac;

    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void anExecutionTest() throws Exception {
        mockMvc.perform(post(URL + "?async=true")
                .contentType(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN)
                .content("print('greetings')"))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", URL + "/1"));
        mockMvc.perform(post(URL + "?async=false")
                .contentType(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN)
                .content("print('greetings')"))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", URL + "/2"));
    }
    @Test
    public void catchScriptCompilationException() throws Exception {
        String script = "print(";
        String errorMessage = null;
        try {
            compiler.compile(script);
        } catch (ScriptException e) {
            errorMessage = e.getMessage();
        }
        mockMvc.perform(post(URL + "?async=true")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content(script))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
        mockMvc.perform(post(URL + "?async=false")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content(script))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }
    @Test
    public void getAllScriptsTest() throws  Exception{
        String uri = "/api/scripts";
        mockMvc.perform(get(uri).accept("application/hal+json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/scripts"))
                .andExpect(jsonPath("$._links.asyncEval.href").value("http://localhost/api/scripts?async=true"))
                .andExpect(jsonPath("$._links.syncEval.href").value("http://localhost/api/scripts?async=false"));
    }
    @Test
    public void getSingleScriptTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(URL + "?async=true")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content("while(true){}"))
                .andExpect(status().isCreated())
                .andReturn();
        String location = "http://localhost" + mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location).accept("application/hal+json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(location))
                .andExpect(jsonPath("$._links.body.href").value(location + "/body"))
                .andExpect(jsonPath("$._links.output.href").value(location + "/output"))
                .andExpect(jsonPath("$._links.stop.href").value(location));
        mockMvc.perform(delete(location))
                .andExpect(status().isOk());
        mockMvc.perform(get(location).accept("application/hal+json"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void outTest() throws Exception {
        mockMvc.perform(post(URL + "?async=true")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content("print(3)"))
                .andExpect(status().isCreated());
    }

    @Test
    public void getIllegalStateByGettingNotExistingBodyOrOutput() throws Exception {
        mockMvc.perform(get(URL + "/10000/body"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(URL + "/10000/output"))
                .andExpect(status().isNotFound());
    }

}
