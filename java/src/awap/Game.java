package awap;

import com.google.common.base.Optional;
import java.util.List;

public class Game {
  private State state;
  private Integer number;

  public Optional<Move> updateState(State newState) {
    if (newState.getError().isPresent()) {
      Logger.log(newState.getError().get());
      return Optional.absent();
    }

    if (newState.getMove() != -1) {
      return Optional.fromNullable(findMove());
    }

    state = newState;
    if (newState.getNumber().isPresent()) {
      number = newState.getNumber().get();
    }

    return Optional.absent();
  }

  private Move findMove() {
    int N = state.getDimension();
    Heuristic heuristic = new DistanceHeuristic();
    List<Block> blocks = state.getBlocks().get(number);

    double bestScore = -Double.MAX_VALUE;
    Move bestMove = null;
    for (int x = 0; x < N; x++) {
      for (int y = 0; y < N; y++) {
        for (int rot = 0; rot < 4; rot++) {
          for (int i = 0; i < blocks.size(); i++) {
            Block rotBlock = blocks.get(i).rotate(rot);
            Point point = new Point(x, y);
            if (canPlace(rotBlock, point)) {
//              new DistanceHeuristic().evaluate(state, number, rotBlock, point);
              double score = heuristic.evaluate(state, number, rotBlock, point);
              if (score > bestScore) {
                bestMove = new Move(i, rot, x, y);
                bestScore = score;
              }
            }
          }
        }
      }
    }

    return bestMove != null ? bestMove : new Move(0, 0, 0, 0);
  }

  private int getPos(int x, int y) {
    return state.getBoard().get(x).get(y);
  }

  private boolean canPlace(Block block, Point p) {
    boolean onAbsCorner = false, onRelCorner = false;
    int N = state.getDimension() - 1;

    Point[] corners = {new Point(0, 0), new Point(N, 0), new Point(N, N), new Point(0, N)};

    Point corner = corners[number];

    for (Point offset : block.getOffsets()) {
      Point q = offset.add(p);
      int x = q.getX(), y = q.getY();

      if (x > N || x < 0 || y < 0 || y > N || getPos(x, y) >= 0 || getPos(x, y) == -2 ||
          (x > 0 && getPos(x - 1, y) == number) || (y > 0 && getPos(x, y - 1) == number) ||
          (x < N && getPos(x + 1, y) == number) || (y < N && getPos(x, y + 1) == number)) {
        return false;
      }

      onAbsCorner = onAbsCorner || q.equals(corner);
      onRelCorner = onRelCorner || (x > 0 && y > 0 && getPos(x - 1, y - 1) == number) ||
          (x < N && y > 0 && getPos(x + 1, y - 1) == number) ||
          (x > 0 && y < N && getPos(x - 1, y + 1) == number) ||
          (x < N && y < N && getPos(x + 1, y + 1) == number);
    }

    return !((getPos(corner.getX(), corner.getY()) < 0 && !onAbsCorner) ||
        (!onAbsCorner && !onRelCorner));
  }
}
