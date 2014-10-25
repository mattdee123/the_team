package awap;

public interface Heuristic {
  public double evaluate(State state, Block block, Point point);
}
