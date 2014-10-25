package awap;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;

public class Block {
  private List<Point> offsets;

  public Block() {
  }

  public Block(List<Map<String, Integer>> offsets) {
    this.offsets = Lists.transform(offsets, new Function<Map<String, Integer>, Point>() {
          public Point apply(Map<String, Integer> map) {
            return new Point(map);
          }
        });
  }

  public List<Point> getOffsets() {
    return offsets;
  }

  public void setOffsets(List<Point> offsets) {
    this.offsets = offsets;
  }

  public int size() {
    return this.offsets.size();
  }

  public Block rotate(final int rotations) {
    if (rotations == 0) {
      return this;
    }

    Block block = new Block();
    block.setOffsets(Lists.transform(offsets, new Function<Point, Point>() {
      public Point apply(Point p) {
        return p.rotate(rotations);
      }
    }));

    return block;
  }

  public List<Point> getPointsForMove(final Point loc) {
    return Lists.transform(offsets, new Function<Point, Point>() {
      @Override
      public Point apply(Point point) {
        return point.add(loc);
      }
    });
  }
}
