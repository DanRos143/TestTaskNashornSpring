package rest;

/**
 * Created by danros on 21.11.16.
 */
public class ScriptWrapper {
    private String content;
    private StringBuilder output;
    private Status status;

    public ScriptWrapper(String content) {
        this.content = content;
        this.status = Status.Waiting;
        this.output = new StringBuilder();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public StringBuilder getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = new StringBuilder(output);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "content='" + content + '\'' +
                ", status=" + status;
    }
}
