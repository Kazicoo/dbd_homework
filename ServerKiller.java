public class ServerKiller extends ServerPlayer {
    
    public ServerKiller(int id) {
        super(id);
    }

    @Override 
    public String toString() { 
        return "Killer{id=" + getId() + ", relativeLocation=" + getRelativeLocation() + "}"; 
    }
}
