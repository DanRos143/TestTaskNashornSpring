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
            emitter.send(msg + "\n");
            sb.append(msg);
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return null;
    }
}
