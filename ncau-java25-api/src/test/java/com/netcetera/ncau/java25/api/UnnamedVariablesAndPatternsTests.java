package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.Test;

class UnnamedVariablesAndPatternsTests {

  @Test
  void exceptionParameter() {
    assertEquals(42, safeParse("42", -1));
    assertEquals(0, safeParse("42.0", 0));
  }

  private static int safeParse(String s, int defaultValue) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException _) {
      // if this happens often it is still preferable to validate the input first (not with a regex!)
      return defaultValue;
    }
  }

  @Test
  void forEachParameter() throws IOException {
    // #resources(String) may be preferable, but I need an example
    Enumeration<URL> manifests = UnnamedVariablesAndPatternsTests.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
    int manifestCount = count(manifests::asIterator);
    assertTrue(manifestCount > 0, "no manifets found");
  }

  private static <T> int count(Iterable<T> i) {
    int total = 0;
    for (var _ : i) {
      total = Math.addExact(total, 1);
    }
    return total;
  }

  @Test
  void localVariable() {
    Queue<Integer> xyzCoordinates = new ArrayDeque<>(List.of(1, 2, -1, 3, 4, -2));
    assertEquals(List.of(new Point(1, 2), new Point(3, 4)), drainPoints(xyzCoordinates));
  }

  private static List<Point> drainPoints(Queue<Integer> xyzCoordinates) {
    List<Point> points = new ArrayList<>((xyzCoordinates.size() * 2 / 3) + 1);
    while (xyzCoordinates.size() >= 3) {

      int x = xyzCoordinates.remove();
      int y = xyzCoordinates.remove();
      var _ = xyzCoordinates.remove(); // use local variable to avoid static analysis warnings
      points.add(new Point(x, y));
    }
    return points;
  }

  record Point(int x, int y) {

  }

  @Test
  void tryBlock() {
    var lock = new ReentrantLock();

    // convenience of synchronized with Lock
    try (var _ = autoRelease(lock)) {
      // try-with-resources just used to close
      assertTrue(true);
    }

  }

  private static AutoReleaseLock autoRelease(Lock lock) {
    lock.lock();
    return new AutoReleaseLock(lock);
  }

  static final class AutoReleaseLock implements AutoCloseable {

    private final Lock lock;

    AutoReleaseLock(Lock lock) {
      this.lock = lock;
    }

    @Override
    public void close() {
      this.lock.unlock();
    }

  }

}
