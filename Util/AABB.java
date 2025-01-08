package Util;

public class AABB {
  private final double x;
  private final double y;
  private final double w;
  private final double h;

  public AABB(double x, double y, double w, double h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getW() {
    return w;
  }

  public double getH() {
    return h;
  }

  public Pair<Double> getCenter() {
    return new Pair<>(x + w / 2, y + h / 2);
  }

  public Pair<Double> getTopLeft() {
    return new Pair<>(x, y);
  }

  public Pair<Double> getTopRight() {
    return new Pair<>(x + w, y);
  }

  public Pair<Double> getBottomRight() {
    return new Pair<>(x + w, y + h);
  }

  public Pair<Double> getBottomLeft() {
    return new Pair<>(x, y + h);
  }

  public boolean isColliding(AABB other) {
    return x     < other.x + other.w &&
           x + w > other.x           &&
           y     < other.y + other.h &&
           y + h > other.y;
  }
}
