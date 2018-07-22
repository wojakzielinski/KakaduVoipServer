package tcpMessage.model;

/**
 * Created by Szymon on 18.07.2018.
 */
//Temporary Class TempRoom for rooms properties
public class TempRoom{
    private String name,owner;

    public TempRoom(String name, String owner){
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}