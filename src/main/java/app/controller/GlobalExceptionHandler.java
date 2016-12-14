package app.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.script.ScriptException;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException() {
        log.error("requested resource is not found");
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ScriptException.class)
    public ResponseEntity handleScriptException(ScriptException se) {
        log.error("source string is not valid script");
        return ResponseEntity.badRequest().body(se.getMessage());
    }
}
