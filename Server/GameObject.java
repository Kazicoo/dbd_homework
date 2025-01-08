package Server;


public abstract class GameObject {
  private final int id;

  private int x = 0;
  private int y = 0;

  public GameObject(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public abstract void update(double elapsedMs);

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void moveX(int dx) {
    x += dx;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void moveY(int dy) {
    y += dy;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void move(int dx, int dy) {
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
