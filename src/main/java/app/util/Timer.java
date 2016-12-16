package app.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
public class Timer {
    private long start;
    private long finish;

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public String stop() {
        this.finish = System.currentTimeMillis();
        return Duration.of(finish - start, ChronoUnit.MILLIS)
                .toString();
    }
}
