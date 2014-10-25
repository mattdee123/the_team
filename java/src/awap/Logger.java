package awap;

import java.util.List;

public class Logger {
  public static void log(Object message) {
    System.err.println(message.toString());
  }

  public static <E> void logMatrix(List<List<E>> matrix) {
    for (List<E> row : matrix) {
      for (E val : row) {
        System.err.print(val.toString() + " ");
      }
      System.err.println();
    }
    System.err.println();
  }
}
