

public class ServerHuman extends ServerPlayer {
    static final int DEFAULT_MOVE_SPEED = 6;    
    static final int FAST_MOVE_SPEED    = 9;
    static final int SLOW_MOVE_SPEED    = 1;

    private int health = 2;
    private int moveSpeedEffectTime;

    private void setFastMoveSpeed() {
        this.setMoveSpeed(FAST_MOVE_SPEED);
    }

    private void setSlowMoveSpeed() {
        this.setMoveSpeed(SLOW_MOVE_SPEED);
    }

    public ServerHuman(int id, ServerGame game) {
        super(id, game);
        super.resetMoveSpeed();
    }

    public void getHurt() {
        setHealth(health-1);

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
}
