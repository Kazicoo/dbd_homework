public class serverPlayer extends ServerGameObject {
    private int relativeLocation;   

    public serverPlayer(int id) {
        super(id);
        this.relativeLocation = -1; 
    }

    public void setRelativeLocation(int location) {
        this.relativeLocation = location;
    }

    public int getRelativeLocation() {
        return this.relativeLocation;
    }

    @Override
    public String toString() {
        return "Player{id=" + getId() + ", relativeLocation=" + relativeLocation + "}";
    }
}
