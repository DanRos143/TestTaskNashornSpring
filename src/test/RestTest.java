import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rest.Application;
import rest.ScriptEvalController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Created by danros on 25.11.16.
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScriptEvalController.class)
public class RestTest {



    private MockMvc mockMvc;

    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ScriptEvalController()).build();
    }

    @Test
    public void testSayHelloWorld() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/scripts").content("print(11)"))
                .andExpect(status().is(201)).andReturn();
        String trackURL = mvcResult.getResponse().getHeader("TrackURL");
        this.mockMvc.perform(get(trackURL)).andExpect(status().is2xxSuccessful());
    }


}
