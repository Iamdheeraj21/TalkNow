package Project;

public class Group
{
    private String groupname;
    private String groupdescription;
    private String createdby;
    private int groupimage;


    public Group(String groupname, String groupdescription, String createdby, int groupimage) {
        this.groupname = groupname;
        this.groupdescription = groupdescription;
        this.createdby = createdby;
        this.groupimage = groupimage;
    }

    public Group(){
        //Default Constructor
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupdescription() {
        return groupdescription;
    }

    public void setGroupdescription(String groupdescription) {
        this.groupdescription = groupdescription;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public int getGroupimage() {
        return groupimage;
    }

    public void setGroupimage(int groupimage) {
        this.groupimage = groupimage;
    }
}
