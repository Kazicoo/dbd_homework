public class ServerKiller extends serverPlayer {
    
    public ServerKiller(int id) {
        super(id);
        initializeSpeed();
    }

    private void initializeSpeed() {
        setSpeed(8);
    }
}
