package Server;


public class AABB {
  private int x;
  private int y;
  private int w;
  private int h;

  public AABB(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public boolean isColliding(AABB other) {
    return x     < other.x + other.w &&
           x + w > other.x           &&
           y     < other.y + other.h &&
           y + h > other.y;
  }
}
