package app.controller;

import app.Application;
import app.compiler.ScriptCompiler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import app.compiler.ScriptCompilerImpl;
import app.service.ScriptServiceImpl;

import javax.script.ScriptException;
import javax.servlet.ServletOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ScriptServiceImpl.class, ScriptCompilerImpl.class, ScriptController.class, Application.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerTest {

    private MockMvc mockMvc;

    @Autowired
    ScriptCompiler compiler;

    @Autowired
    WebApplicationContext wac;

    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /*@Test
    public void anExecutionTest() throws Exception {
        mockMvc.perform(post("/api/scripts/async")
                .contentType(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN)
                .content("print('greetings')"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/scripts/1"))
                .andExpect(request().asyncStarted());
        mockMvc.perform(post("/api/scripts/sync")
                .contentType(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN)
                .content("print('greetings')"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/scripts/2"))
                .andExpect(content().string("greetings\n"));
    }*/
    /*@Test
    public void catchScriptCompilationException() throws Exception {
        String script = "print(";
        String errorMessage = null;
        try {
            compiler.compile(script);
        } catch (ScriptException e) {
            errorMessage = e.getMessage();
        }
        mockMvc.perform(post("/api/scripts/async")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content(script))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage + "\n"));
        mockMvc.perform(post("/api/scripts/sync")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content(script))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }*/
    @Test
    public void catchRuntimeScriptException() throws Exception {
        String script = "log(3)";
        String errorMessage = null;
        try {
            compiler.compile(script).eval();
        } catch (ScriptException e) {
            errorMessage = e.getMessage();
        }
        mockMvc.perform(post("/api/scripts/async")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content(script))
                .andExpect(status().isCreated());
        /*mockMvc.perform(post("/api/scripts/sync")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content(script))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(errorMessage));*/
    }
    @Test
    public void getAllScriptsTest() throws  Exception{
        String uri = "/api/scripts";
        mockMvc.perform(get(uri).accept("application/hal+json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/scripts"))
                .andExpect(jsonPath("$._links.asyncExecution.href").value("http://localhost/api/scripts/async"))
                .andExpect(jsonPath("$._links.syncExecution.href").value("http://localhost/api/scripts/sync"));
    }
    /*@Test
    public void getSingleScriptTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/scripts/async")
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
                .andExpect(status().isNotFound());*//*
    }*/
    @Test
    public void outTest() throws Exception {
        mockMvc.perform(post("/api/scripts/async")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content("print(3)"))
                .andExpect(status().isCreated())
                .andReturn();

    }
}
