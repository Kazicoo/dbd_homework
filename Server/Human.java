package Server;


public class Human extends Player {
  public static final double NORMAL_SPEED = 18;
  public static final double FAST_SPEED   = 25;
  public static final double SLOW_SPEED   = 5;
  public static final double HEAL_TARGET  = 10;

  private int health = 2;
  private int fallen = 0;

  private Human  healing    = null;
  private double healStatus = 0;

  @Override
  public double normalSpeed() {
    return NORMAL_SPEED;
  }

  public Human(int id, Role role) {
    super(id, role);
    super.setSpeed(NORMAL_SPEED);
  }

  @Override
  public void update(double elapsed) {
    if (healing != null) {
      healStatus += elapsed;
      if (healStatus >= HEAL_TARGET) {
        healing.health = 2;
        healStatus = 0;

        setSpeed(NORMAL_SPEED);
        healing.setSpeed(NORMAL_SPEED);

        Server.instance.sendMessage("Healed;" + healing.getId());

        healing = null;
      }
    }

    super.update(elapsed);
  }

  public int getHealth() {
    return health;
  }

  public boolean isDead() {
    return fallen >= 2;
  }

  public boolean isFallen() {
    return health <= 0;
  }

  public void healing(Human target) {
    if (!target.isFallen() || isFallen() || isDead() || healing != null)
      return;

    healing = target;
    Server.instance.sendMessage("Healing;" + getId() + ";" + target.getId());

    setSpeed(0);
    target.setSpeed(0);
  }

  public void stopHealing() {
    if (healing == null)
      return;

    setSpeed(NORMAL_SPEED);
    healing.setSpeed(SLOW_SPEED);

    healing = null;
    healStatus = 0;
  }

  public void getHurt() {
    health--;
    healStatus = 0;

    if (health > 0) {
      setSpeed(FAST_SPEED, 2);
      Server.instance.sendMessage("Hit;" + getId());
    }

    else if (fallen < 2) {
      setSpeed(SLOW_SPEED);
      Server.instance.sendMessage("Fall;" + getId());
      fallen++;
    }

    else {
      setSpeed(0);
      Server.instance.sendMessage("Dead;" + getId());
      fallen++;
    }
  }
}
