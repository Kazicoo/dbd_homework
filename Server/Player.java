package Server;

import Util.Pair;
import Util.AABB;


public abstract class Player extends GameObject {
  public static final int SIZE = 50;

  public enum Role {
    Killer,
    Player1,
    Player2,
    Player3,
  }

  public enum Direction {
    Up,
    Down,
    Left,
    Right,
  }

  public final Role role;

  private boolean   up        = false;
  private boolean   down      = false;
  private boolean   left      = false;
  private boolean   right     = false;
  private Direction facing    = Direction.Down;
  private double    speed     = 0;
  private double    speedLast = 0;

  public Player(int id, Role role) {
    super(id);
    this.role = role;
    this.resetSpeed();
  }

  @Override
  public void update(double elapsed) {
    if (speedLast > 0) {
      speedLast -= elapsed;
      if (speedLast <= 0)
        resetSpeed();
    }

    double dx = (double)getDx() * getSpeed() * elapsed;
    double dy = (double)getDy() * getSpeed() * elapsed;

    if (dx == 0 && dy == 0)
      return;

    int oldX = (int)getX();
    int oldY = (int)getY();

    move(dx, dy);

    boolean colliding = false;

    MapItem items[] = getNearbyItems();
    for (int j=0; j<items.length && !colliding; j++)
      if (items[j] != null && isColliding(items[j]))
        colliding = true;

    Player players[] = Server.instance.getPlayers();
    for (int j=0; j<players.length && !colliding; j++)
      if (players[j] != null && players[j] != this && isColliding(players[j]))
        colliding = true;

    if (colliding)
      move(-dx, -dy);

    int newX = (int)getX();
    int newY = (int)getY();

    if (oldX != newX || oldY != newY)
      Server.instance.sendMessage("Move;" + getId() + ";" + newX + ";" + newY);
  }

  @Override
  public AABB getAABB() {
    return new AABB(getX(), getY(), SIZE, SIZE);
  }

  public Direction getFacing() {
    return facing;
  }

  public int getDx() {
    return (left ? -1 : 0) + (right ? 1 : 0);
  }

  public int getDy() {
    return (up ? -1 : 0) + (down ? 1 : 0);
  }

  public abstract double normalSpeed();

  public double getSpeed() {
    return speed;
  }

  protected void setSpeed(double speed, double duration) {
    this.speed = speed;
    speedLast = duration;
  }

  protected void setSpeed(double speed) {
    setSpeed(speed, 0);
  }

  protected void resetSpeed() {
    setSpeed(normalSpeed());
  }

  public void updateDirection(Direction dir, boolean pressed) {
    up    = dir == Direction.Up    ? pressed : up;
    down  = dir == Direction.Down  ? pressed : down;
    left  = dir == Direction.Left  ? pressed : left;
    right = dir == Direction.Right ? pressed : right;
  }

  public MapItem[] getNearbyItems() {
    int gx = (int)getX() / Server.GRID_SIZE;
    int gy = (int)getY() / Server.GRID_SIZE;

    int     count   = 0;
    MapItem items[] = new MapItem[25];

    for (int x=-2; x<=2; x++)
      for (int y=-2; y<=2; y++)
        // Ensure the item is not null
        if (Server.instance.getMapItem(gx+x, gy+y) instanceof MapItem i)
          items[count++] = i;

    return items;
  }

  public boolean inRange(Player other, double range, double angle) {
    double base_angle = switch (facing) {
      case Up    -> 90;
      case Down  -> 270;
      case Left  -> 180;
      case Right -> 0;
    };

    // Circle collision detection
    {
      Pair<Double> center = getAABB().getCenter();
      double vx   = center.getA() - getX();
      double vy   = center.getB() - getY();
      double dist = Math.sqrt(vx*vx + vy*vy);

      if (dist-(SIZE/2) <= range) {
        double theta = Math.toDegrees(Math.acos(vx / dist));
        while (theta < 0  ) theta += 360;
        while (theta > 360) theta -= 360;

        if (Math.abs(theta - base_angle) > angle)
          return false;
      }
    }

    // Corner point collision detection
    {
      AABB aabb = other.getAABB();

      @SuppressWarnings("unchecked")
      Pair<Integer> points[] = new Pair[] {
        aabb.getTopLeft(),
        aabb.getTopRight(),
        aabb.getBottomRight(),
        aabb.getBottomLeft(),
      };

      // check if any corner of the other object is within the range and angle
      for (Pair<Integer> pt : points) {
        double vx   = pt.getA() - getX();
        double vy   = pt.getB() - getY();
        double dist = Math.sqrt(vx*vx + vy*vy);

        if (dist <= range) {
          double theta = Math.toDegrees(Math.acos(vx / dist));
          while (theta < 0  ) theta += 360;
          while (theta > 360) theta -= 360;

          if (Math.abs(theta - base_angle) <= angle)
            return true;
        }
      }
    }

    return false;
  }
}
