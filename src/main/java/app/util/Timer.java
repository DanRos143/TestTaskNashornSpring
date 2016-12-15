package app.util;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Value
@NoArgsConstructor
public class Timer {
    @NonFinal private long start;
    @NonFinal private long finish;

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public String stop() {
        this.finish = System.currentTimeMillis();
        return Duration.of(finish - start, ChronoUnit.MILLIS)
                .toString();
    }
}
