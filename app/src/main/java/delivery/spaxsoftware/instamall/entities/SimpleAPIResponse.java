package delivery.spaxsoftware.instamall.entities;

public class SimpleAPIResponse {

    //User API callback responses
    private String Status;
    private String Message;
    public SimpleAPIResponse() {
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

}
