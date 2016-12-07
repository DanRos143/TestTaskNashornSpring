package app.script;

import org.springframework.hateoas.Identifiable;


public class Script implements Identifiable<Integer> {
    private Integer id;
    private ScriptStatus status;
    private String body;
    private StringBuilder output;
    private long executionTime;
    private Thread thread;


    public Script(Integer id, String body) {
        this.id = id;
        this.body = body;
        this.output = new StringBuilder();
        this.status = ScriptStatus.Waiting;
    }
    public Script(){
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ScriptStatus getStatus() {
        return status;
    }

    public void setStatus(ScriptStatus status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public StringBuilder getOutput() {
        return output;
    }

    public void setOutput(StringBuilder output) {
        this.output = output;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
