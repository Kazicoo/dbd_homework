public class ServerGenerator extends ServerGameObject {       
    private int relativeLocation;  

    public ServerGenerator(int id) {
        super(id);
        this.relativeLocation = -1; 
    }

    public void setRelativeLocation(int location) {
        this.relativeLocation = location;
    }

    public int getRelativeLocation() {
        return this.relativeLocation;
    }
}