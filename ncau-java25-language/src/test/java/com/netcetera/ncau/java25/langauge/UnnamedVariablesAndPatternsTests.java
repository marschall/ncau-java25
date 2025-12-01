package com.netcetera.ncau.java25.langauge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.Test;

import com.netcetera.ncau.java25.langauge.UnnamedVariablesAndPatternsTests.BuildResult.Error;
import com.netcetera.ncau.java25.langauge.UnnamedVariablesAndPatternsTests.BuildResult.Failure;
import com.netcetera.ncau.java25.langauge.UnnamedVariablesAndPatternsTests.BuildResult.Success;

class UnnamedVariablesAndPatternsTests {

  static final VarHandle LONG_BA_BE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN).withInvokeExactBehavior();

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
  void arrayGet() {
    byte[] array = new byte[8];
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
      long _ = (long) LONG_BA_BE.get(array, -1);
    });
  }

  @Test
  void tryBlock() {
    var lock = new ReentrantLock();

    // convenience of synchronized with Lock
    try (var _ = autoRelease(lock)) {
      // try-with-resources just used to close
      assertTrue(true);
    }

    // scopes of spans are other examples
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

  @Test
  void lambdaParameter() {
    Map<String, String> cache = new HashMap<>();

    String value = cache.computeIfAbsent("key", _ -> "default");

    assertEquals("default", value);
    assertEquals(1, cache.size());
  }

  @Test
  void patternVariable() {
    assertTrue(isSuccessful(new Success()));
  }
  
  private static boolean isSuccessful(BuildResult result) {
    return switch (result) {
      case Error _ -> false;
      case Failure _ -> false;
      case Success _ -> true;
    };
  }

  abstract static sealed class BuildResult {

    static final class Success extends BuildResult {

    }

    static final class Error extends BuildResult {

    }

    static final class Failure extends BuildResult {

    }

  }

}
