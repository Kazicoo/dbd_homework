public class serverPlayer {
    private int playerId;          
    private int relativeLocation;   

    public serverPlayer(int id) {
        this.playerId = id;
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
        return "Player{id=" + playerId + ", relativeLocation=" + relativeLocation + "}";
    }
}
