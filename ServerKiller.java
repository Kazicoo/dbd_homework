public class ServerKiller extends serverPlayer {
    
    public ServerKiller(int id) {
        super(id);
    }

    @Override 
    public String toString() { 
        return "Killer{id=" + getId() + ", relativeLocation=" + getRelativeLocation() + "}"; 
    }
}
