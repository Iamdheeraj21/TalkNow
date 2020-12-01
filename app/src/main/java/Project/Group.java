package Project;

public class Group
{
    private String groupname;
    private String description;
    private String groupimage;


    public Group(String groupname, String description, String groupimage) {
        this.groupname = groupname;
        this.description = description;
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
