package app.util;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.function.Function;

public class AsyncPrint implements Function<Object, Void> {
    private ResponseBodyEmitter emitter;
    private StringBuilder sb;

    public AsyncPrint(ResponseBodyEmitter emitter, StringBuilder sb) {
        this.emitter = emitter;
        this.sb = sb;
    }

    @Override
    public Void apply(Object msg) {
        try {
            sb.append(msg);
            emitter.send(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
