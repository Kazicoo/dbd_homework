





public class ServerHuman extends ServerPlayer {
    static final int DEFAULT_MOVE_SPEED = 7;    
    static final int FAST_MOVE_SPEED    = 10;
    static final int SLOW_MOVE_SPEED    = 0;

    private int health = 2;
    private int moveSpeedEffectTime;

    private void setFastMoveSpeed() {
        this.setMoveSpeed(FAST_MOVE_SPEED);
    }

    private void setSlowMoveSpeed() {
        this.setMoveSpeed(SLOW_MOVE_SPEED);
    }

    public ServerHuman(int id, ServerGame game, String role) {
        super(id, game, role);
        super.resetMoveSpeed();
    }

    public void getHurt() {
        setHealth(health-1);
        game.setHealthStatus(health, getId());
        // game.sendMessage("updateGameObject;health;" + health + ";" + game.chars[i]);

        if (health > 0) {
            setFastMoveSpeed();
            // 50毫秒一幀 = 2秒
            moveSpeedEffectTime = 40;   // 50*40 == 2000, 是 40, 不是 100
        } 
        
        else {
            // 倒地
            setSlowMoveSpeed();
            // 永遠不會自然被清掉
            moveSpeedEffectTime = -1;
        }
    }

    @Override
    public int defaultSpeed() {
        return DEFAULT_MOVE_SPEED; 
    }
    
    @Override
    public void update() {
        if (moveSpeedEffectTime > 0) {
            moveSpeedEffectTime -= 1;
            if (moveSpeedEffectTime == 0) {
                resetMoveSpeed();
            }
        }

        super.update();
    } 

    public int getHealth() {
        return this.health;
    }

    public int setHealth(int health) {
        return this.health = health;
    }   

    public boolean canInteractGenerator(ServerGenerator generator) {
        return ServerGame.aabb_collision(
            // self top left
            getX() - ServerGame.GRID_SIZE / 2, 
            getY() - ServerGame.GRID_SIZE / 2, 
            // self bottom right
            getX() + ServerGame.GRID_SIZE / 2,
            getY() + ServerGame.GRID_SIZE / 2,
            // other top left
            generator.getX() - ServerGame.GRID_SIZE,
            generator.getY() - ServerGame.GRID_SIZE,
            // other bottom right
            generator.getX() + 3 * ServerGame.GRID_SIZE,
            generator.getY() + 2 * ServerGame.GRID_SIZE);
    }

    public boolean canInteractWindow(ServerWindow window) {
        return ServerGame.aabb_collision(
            // self top left
            getX() - ServerGame.GRID_SIZE / 2, 
            getY() - ServerGame.GRID_SIZE / 2, 
            // self bottom right
            getX() + ServerGame.GRID_SIZE / 2,
            getY() + ServerGame.GRID_SIZE / 2,
            // other top left
            window.getX() - ServerGame.GRID_SIZE,
            window.getY() - ServerGame.GRID_SIZE,
            // other bottom right
            window.getX() + ServerGame.GRID_SIZE,
            window.getY() + ServerGame.GRID_SIZE);
    }
}
