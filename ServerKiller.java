

public class ServerKiller extends ServerPlayer {
    static final int DEFAULT_MOVE_SPEED = 8;    
    static final int SLOW_MOVE_SPEED = 3;

    static final double ATTACK_RANGE = 2.75;
    static final double ATTACK_ANGLE = 135;

    int moveSpeedEffectTime = 0;
    
    // 50毫秒一幀 = 2秒
    static final int ATTACK_CD = (int)(2 * ServerGame.FRAME_PER_SEC);
    private int attackCounter = 0;


    public ServerKiller(int id, ServerGame game) {
        super(id, game);
        super.resetMoveSpeed();
    }

    private boolean canAttack() {
        return attackCounter >= ATTACK_CD;
    }

    public void resetAttackCounter() {
        attackCounter = 0;
    }

    private void setSlowMoveSpeed() {
        setMoveSpeed(SLOW_MOVE_SPEED);
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

        if (attackCounter < ATTACK_CD) {
            attackCounter += 1;
        }

        super.update();
    }

    public void attack() {
        System.out.println(canAttack());
        if (!canAttack()) 
            return;

        resetAttackCounter();
        System.out.println("attackCounter: " + attackCounter);

        setSlowMoveSpeed();
        System.out.println("moveSpeed: " + getMoveSpeed());

        moveSpeedEffectTime = (int)(2 * ServerGame.FRAME_PER_SEC);
        System.out.println("moveSpeedEffectTime: " + moveSpeedEffectTime);

        // 送封包給客戶端
        game.sendMessage("attack;" + getFacing());

        for (ServerHuman human : game.getHumans()) {
            System.out.println("inRange: " + inRange(human.getX(), human.getY(), ATTACK_RANGE, ATTACK_ANGLE));
            if (inRange(human.getX(), human.getY(), ATTACK_RANGE * ServerGame.GRID_SIZE, ATTACK_ANGLE)) {
                human.getHurt();
                break;
            }
        }
    }
}
