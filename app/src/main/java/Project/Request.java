package Project;

public class Request {
    private String id;
    private String requeststatus;

    public Request(String id,String requeststatus)
    {
        this.id=id;
        this.requeststatus=requeststatus;
    }
    public Request(){
        //Todo
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequeststatus() {
        return requeststatus;
    }

    public void setRequeststatus(String requeststatus) {
        this.requeststatus = requeststatus;
    }
}
