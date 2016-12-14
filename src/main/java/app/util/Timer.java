package app.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Timer {
    private long start;
    private long finish;

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public long stop() {
        this.finish = System.currentTimeMillis();
        return finish - start;
    }
}
