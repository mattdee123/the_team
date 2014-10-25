package awap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DistanceHeuristic implements Heuristic {
  private static final List<Point> fourPoint =
          ImmutableList.of(new Point(-1, 0), new Point(1, 0), new Point(0, -1), new Point(0, 1));

  private static final double GREEDY_SCALE = 1000.0;
  private static final double CLOSEST_BONUS = 10.0;
  private static final double OTHER_TEAM_SCORE_SCALE = 0.5;

  private List<Point> getNeighbors(Point p, List<List<Integer>> board) {
    List<Point> nbrs = new ArrayList<>();
    for (int xOff = -1; xOff <= 1; xOff++) {
      for (int yOff = -1; yOff <= 1; yOff++) {
        if (xOff == 0 && yOff == 0) {
          continue;
        }
        int newY = p.getY() + yOff;
        int newX = p.getX() + xOff;
        if (newX >= 0 && newY >= 0 && newX < board.size() && newY < board.size() &&
                board.get(newY).get(newX) == -1) {
          nbrs.add(new Point(newX, newY));
        }
      }
    }
    return nbrs;
  }

  private List<List<Integer>> distanceFrom(int team, List<List<Integer>> board) {
    Set<Point> frontier = Sets.newHashSet();
    List<List<Integer>> distances = new ArrayList<>();
    for (List<Integer> row : board) {
      List<Integer> newRow = new ArrayList<>();
      for (Integer val : row) {
        newRow.add(Integer.MAX_VALUE);
      }
      distances.add(newRow);
    } // init frontier
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.size(); j++) {
        if (board.get(i).get(j).equals(team)) {
          frontier.add(new Point(j, i));
        }
      }
    }
    int distance = 0;
    while (!frontier.isEmpty()) {
      Set<Point> newFrontier = Sets.newHashSet();

      for (Point point : frontier) {
        distances.get(point.getY()).set(point.getX(), distance);
      }
      for (Point point : frontier) {
        List<Point> nbrs = getNeighbors(point, board);
        for (Point nbr : nbrs) {
          if (distances.get(nbr.getY()).get(nbr.getX()).equals(Integer.MAX_VALUE) && canPlace(board, team, nbr)) {
            newFrontier.add(nbr);
          }
        }
      }
      distance++;
      frontier = newFrontier;
    }
    return distances;
  }

  private boolean canPlace(List<List<Integer>> board, int team, Point pos) {
    for (Point point : fourPoint) {
      Point nbr = pos.add(point);
      int newY = nbr.getY();
      int newX = nbr.getX();
      if (newX >= 0 && newY >= 0 && newX < board.size() && newY < board.size()) {
        if (board.get(newY).get(newX) == team) {
          return false;
        }
      }
    }
    return true;
  }

  public double teamScore(List<List<Integer>> myTeam, List<List<Integer>> mins,
                          List<List<Integer>> newBoard, int team) {
    double result = 0.0;

    for (int i = 0; i < myTeam.size(); i++) {
      for (int j = 0; j < myTeam.get(i).size(); j++) {
        int myDist = myTeam.get(i).get(j);
        int minDist = mins.get(i).get(j);

        if (myDist == 0.0) {
          result += 1.0;
        }
        if (minDist <= 0 || minDist == Integer.MAX_VALUE || myDist == Integer.MAX_VALUE) {
          continue;
        }
        if (!canPlace(newBoard, team, new Point(j, i))) {
          continue;
        }
        if (myDist == minDist) {
          result += CLOSEST_BONUS;
        }
        result += ((double) minDist) / myDist;
      }
    }
    return result;
  }

  @Override
  public double evaluate(State state, int team, Block block, Point point) {
    List<List<Integer>> newBoard = state.playMove(team, block, point);
    List<List<List<Integer>>> teamDistances = new ArrayList<>();

    for (int i = 0; i < 4; i++) {
      teamDistances.add(distanceFrom(i, newBoard));
    }

    List<List<Integer>> myTeam = teamDistances.get(team);

    List<List<Integer>> mins = new ArrayList<>();
    for (List<Integer> row : myTeam) {
      List<Integer> newRow = new ArrayList<>();
      for (Integer val : row) {
        newRow.add(Integer.MAX_VALUE);
      }
      mins.add(newRow);
    }

    for (int i = 0; i < mins.size(); i++) {
      for (int j = 0; j < mins.get(i).size(); j++) {
        int min = Integer.MAX_VALUE;
        for (int k = 0; k < 4; k++) {
          if (min > teamDistances.get(k).get(i).get(j)) {
            min = teamDistances.get(k).get(i).get(j);
          }
        }
        mins.get(i).set(j, min);
      }
    }

    double myTeamScore = teamScore(myTeam, mins, newBoard, team);

    double otherTeamScore = 0.0;
    for (int i = 0; i < 4; i++) {
      if (i != team) {
        otherTeamScore += teamScore(teamDistances.get(i), mins, newBoard, i);
      }
    }

    double result = myTeamScore - (otherTeamScore * OTHER_TEAM_SCORE_SCALE);

    Logger.log(result);
    double greedy = new GreedyHeuristic().evaluate(state, team, block, point);
    return result + greedy * GREEDY_SCALE;
  }
}
