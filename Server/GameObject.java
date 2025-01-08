package Server;

import Util.AABB;


public abstract class GameObject {
  private final int id;

  private double x = 0;
  private double y = 0;

  public GameObject(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public abstract void update(double elapsedMs);

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void moveX(double dx) {
    x += dx;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public void moveY(double dy) {
    y += dy;
  }

  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void move(double dx, double dy) {
    x += dx;
    y += dy;
  }

  public abstract AABB getAABB();

  public boolean isColliding(GameObject other) {
    AABB self = getAABB();
    AABB othr = other.getAABB();
    return self != null && othr != null && self.isColliding(othr);
  }
}
