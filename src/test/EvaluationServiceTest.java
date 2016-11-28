import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import rest.controller.ScriptEvalController;
import rest.manager.ScriptManager;
import rest.manager.ScriptManagerImpl;
import rest.service.EvaluationService;
import rest.service.EvaluationServiceImpl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.sql.Time;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by danros on 28.11.16.
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EvaluationServiceImpl.class, ScriptManagerImpl.class, ScriptEvalController.class})
public class EvaluationServiceTest {







}
