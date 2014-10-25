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
    List<Block> blocks = state.getBlocks().get(number);

    int maxScore = 0;
    Move maxMove = null;

    for (int x = 0; x < N; x++) {
      for (int y = 0; y < N; y++) {
        for (int rot = 0; rot < 4; rot++) {
          for (int i = 0; i < blocks.size(); i++) {
            Block rotBlock = blocks.get(i).rotate(rot);
            Point point = new Point(x, y);
            if (canPlace(rotBlock, point)) {
              int score = getScore(rotBlock, point);
              if (score > maxScore) {
                maxMove = new Move(i, rot, x, y);
                maxScore = score;
              }
            }
          }
        }
      }
    }

    return maxMove != null ? maxMove : new Move(0, 0, 0, 0);
  }

  private int getScore(Block block, Point point) {
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
