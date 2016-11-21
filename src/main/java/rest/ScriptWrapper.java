package rest;

/**
 * Created by danros on 21.11.16.
 */
public class ScriptWrapper {
    private String content;
    private String response;
    private Status status;

    public ScriptWrapper(String content) {
        this.content = content;
        this.status = Status.Waiting;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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
