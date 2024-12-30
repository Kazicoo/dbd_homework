public class ServerHuman extends ServerPlayer {
    private int health = 2; 

    public ServerHuman(int id) {
        super(id);
    }

    public int getHumanHealth() {
        return this.health;
    }
}
