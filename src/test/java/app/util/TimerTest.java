package app.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.util.concurrent.TimeUnit;


public class TimerTest {


    @Test
    public void lombokValueBehaviourTest() throws InterruptedException {
        Assert.assertNull("Timer class doesn't have set method for start field",
                ReflectionUtils.findMethod(Timer.class, "setStart()"));
        Assert.assertNull("Timer class doesn't have set method for finish field",
                ReflectionUtils.findMethod(Timer.class, "setFinish()"));
    }

    @Test
    public void stringRepresentationTest() throws InterruptedException {
        long stopTime = 2010;
        String pattern = "PT\\d+\\.\\d+S";
        Timer timer = new Timer();
        timer.start();
        TimeUnit.MILLISECONDS.sleep(stopTime);
        String stop = timer.stop();
        System.out.println(stop);
        Assert.assertTrue(stop.matches(pattern));
    }
}
