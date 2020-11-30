package Project;

public class Group
{
    private String groupname;
    private String description;
    private String createdby;
    private String groupimage;
    private String search;


    public Group(String groupname, String description, String createdby, String groupimage,String search) {
        this.groupname = groupname;
        this.description = description;
        this.createdby = createdby;
        this.groupimage = groupimage;
        this.search=search;
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

    public String getGroupimage() {
        return groupimage;
    }

    public void setGroupimage(String groupimage) {
        this.groupimage = groupimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
