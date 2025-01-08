package Util;


public class Pair<T> {
  private final T a;
  private final T b;

  public Pair(T a, T b) {
    this.a = a;
    this.b = b;
  }

  public T getA() {
    return a;
  }

  public T getB() {
    return b;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Pair)) {
      return false;
    }

    Pair<?> p = (Pair<?>) o;
    return a.equals(p.a) && b.equals(p.b);
  }
}
