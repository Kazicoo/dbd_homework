public class ServerGenerator {       
    private int relativeLocation;  
    private final int generatorID; 

    public ServerGenerator(int id) {
        this.generatorID = id;
        this.relativeLocation = -1; 
    }

    public void setRelativeLocation(int location) {
        this.relativeLocation = location;
    }

    public int getRelativeLocation() {
        return this.relativeLocation;
    }

    public int getId() {
        return this.generatorID;
    }
}