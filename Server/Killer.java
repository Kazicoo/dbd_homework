package Server;


public class Killer extends Player {
  public static final double NORMAL_SPEED = 20;
  public static final double SLOW_SPEED   = 10;

  public static final double ATTACK_RANGE = 2.75 * Server.GRID_SIZE;
  public static final double ATTACK_ANGLE = 135;
  public static final double ATTACK_CD    = 1;

  private double attackCd = 0;

  public Killer(int id) {
    super(id, Role.Killer);
    super.setSpeed(NORMAL_SPEED);
  }

  @Override
  public double normalSpeed() {
    return NORMAL_SPEED;
  }

  @Override
  public void update(double elapsed) {
    if (attackCd > 0) {
      attackCd -= elapsed;
      if (attackCd < 0)
        attackCd = 0;
    }

    super.update(elapsed);
  }

  public void attack() {
    if (attackCd > 0)
      return;

    attackCd = ATTACK_CD;
    setSpeed(SLOW_SPEED, 2);

    Server.instance.sendMessage("Attack;" + getFacing());
    for (Human h : Server.instance.getHumans()) {
      if (h == null || h.isDead() || h.isFallen())
        continue;

      if (inRange(h, ATTACK_RANGE, ATTACK_ANGLE)) {
        h.getHurt();
        break;
      }
    }
  }
}
