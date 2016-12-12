package app.aspect;

import app.script.Script;
import app.script.ScriptStatus;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jboss.logging.MDC;

@Aspect
@Log4j2
public class ScriptAspect {

    @Around("within(app.script..*)")
    //@Around("within(com.pragmasoft.myapp.repository..*)")
    public void advice(ProceedingJoinPoint pjp) throws Throwable {
        MDC.put("current", Thread.currentThread());
        log.info("async execution started in {}", MDC.get("current"));
        Script target = (Script) pjp.getTarget();
        target.setStatus(ScriptStatus.Running);
        long start = System.currentTimeMillis();
        pjp.proceed();
        long end = System.currentTimeMillis();
        log.info("async execution finished in {}", MDC.get("current"));
        target.setExecutionTime(end - start);

    }
}
