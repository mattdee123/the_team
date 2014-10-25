package awap;

import java.util.Map;

public class Point {
  private int x;
  private int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Point(Map<String, Integer> pointInfo) {
    this.x = pointInfo.get("x");
    this.y = pointInfo.get("y");
  }

  public Point add(Point p) {
    return new Point(x + p.x, y + p.y);
  }

  public boolean equals(Point p) {
    return x == p.x && y == p.y;
  }

  @Override
  public String toString() {
    return "(" +
             x +
            "," + y +
            ')';
  }

  public Point rotate(int numRotations) {
    switch (numRotations) {
      case 1:
        return new Point(-y, x);
      case 2:
        return new Point(-x, -y);
      case 3:
        return new Point(y, -x);
      default:
        return this;
    }
  }

  public int distance(Point p) {
    return Math.abs(x - p.x) + Math.abs(y - p.y);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Point point = (Point) o;

    if (x != point.x) return false;
    if (y != point.y) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }
}
