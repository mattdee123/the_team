package awap;

import java.util.ArrayList;
import java.util.List;

public class DistanceHeuristic implements Heuristic {

  private List<Point> getNeighbors(Point p, List<List<Integer>> board) {
    List<Point> nbrs = new ArrayList<>();
    for (int xOff = -1; xOff <= 1; xOff++) {
      for (int yOff = -1; yOff <= 1; yOff++) {
        if (xOff == 0 && yOff == 0) continue;
        int newY = p.getY() + yOff;
        int newX = p.getX() + xOff;
        if (newX >= 0 && newY >= 0 && newX < board.size() && newY < board.size() && board.get(newY).get(newX) == -1) {
          nbrs.add(new Point(newX, newY));
        }
      }
    }
    return nbrs;
  }

  private List<List<Integer>> distanceFrom(int team, List<List<Integer>> board) {
    List<Point> frontier = new ArrayList<>();
    List<List<Integer>> distances = new ArrayList<>();
    for (List<Integer> row : board) {
      List<Integer> newRow = new ArrayList<>();
      for (Integer val : row) {
        newRow.add(-1);
      }
      distances.add(newRow);
    } // init frontier
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.size(); j++) {
        if (board.get(i).get(j) == team) {
          frontier.add(new Point(j,i));
        }
      }
    }
    int distance = 0;
    while (!frontier.isEmpty()) {
      List<Point> newFrontier = new ArrayList<>();

      for (Point point : frontier) {
        distances.get(point.getY()).set(point.getX(), distance);
      }
      for (Point point : frontier) {
        List<Point> nbrs = getNeighbors(point, board);
        for (Point nbr : nbrs) {
          if (distances.get(nbr.getY()).get(nbr.getX()) == -1) {
            newFrontier.add(nbr);
          }
        }
      }
      distance++;
      frontier = newFrontier;
    }
    return distances;
  }




  @Override
  public double evaluate(State state, Block block, Point point) {
    int team = state.getNumber().get();
    List<List<Integer>> newBoard = state.playMove(team, block, point);
    return 0.0;
  }
}
