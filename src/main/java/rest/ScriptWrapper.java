package rest;

/**
 * Created by danros on 21.11.16.
 */
public class ScriptWrapper {
    private String content;
    private String response;
    private boolean status;

    public ScriptWrapper(String content) {
        this.content = content;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "content='" + content + '\'' +
                ", status=" + status +
                '}';
    }
}
