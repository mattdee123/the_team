package awap;

public interface Heuristic {
  public double evaluate(State state, int team, Block block, Point point);
}
