public class ClientGameObject {
    private final int id;
    private int relativeLocation;  

    public ClientGameObject(int id) {
        this.id = id;
        this.relativeLocation = -1; 
    }

    public int getId() {
        return id;
    }

    public void setRelativeLocation(int location) {
        this.relativeLocation = location;
    }

    public int getRelativeLocation() {
        return relativeLocation;
    }
}
