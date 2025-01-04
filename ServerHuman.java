public class ServerHuman extends serverPlayer {
    private int health = 2; 

    public ServerHuman(int id) {
        super(id);
        initializeSpeed();
    }

    private void initializeSpeed() {
        setSpeed(6);
    }

    public int getHealth() {
        return this.health;
    }

    public int setHealth(int health) {
        return this.health = health;
    }
}
