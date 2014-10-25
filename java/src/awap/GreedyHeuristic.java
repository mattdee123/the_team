package awap;

import java.util.List;

public class GreedyHeuristic implements Heuristic {
  @Override
  public double evaluate(State state, int team, Block block, Point point) {
    List<Point> blockPoints = block.getPointsForMove(point);
    List<Point> bonusPoints = state.getBonusPoints();
    int multiplier = 1;
    for (Point blockPoint : blockPoints) {
      for (Point bonusPoint : bonusPoints) {
        if (blockPoint.equals(bonusPoint)) {
          multiplier = 3;
        }
      }
    }
    return block.size() * multiplier;
  }
}
