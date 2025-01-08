package Util;

public class AABB {
  private final int x;
  private final int y;
  private final int w;
  private final int h;

  public AABB(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getW() {
    return w;
  }

  public int getH() {
    return h;
  }

  public Pair<Integer> getCenter() {
    return new Pair<>(x + w / 2, y + h / 2);
  }

  public Pair<Integer> getTopLeft() {
    return new Pair<>(x, y);
  }

  public Pair<Integer> getTopRight() {
    return new Pair<>(x + w, y);
  }

  public Pair<Integer> getBottomRight() {
    return new Pair<>(x + w, y + h);
  }

  public Pair<Integer> getBottomLeft() {
    return new Pair<>(x, y + h);
  }

  public boolean isColliding(AABB other) {
    return x     < other.x + other.w &&
           x + w > other.x           &&
           y     < other.y + other.h &&
           y + h > other.y;
  }
}
