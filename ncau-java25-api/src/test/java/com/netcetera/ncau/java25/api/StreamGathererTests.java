package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import static java.util.stream.Gatherers.windowSliding;

import org.junit.jupiter.api.Test;

class StreamGathererTests {

  private static final Duration FIVE_SECONDS = Duration.ofSeconds(5L);

  @Test
  void noTerminalOperation() {
    Stream.of(1, 2, 3).peek(
        i -> assertTrue(i < 0));
  }

  @Test
  void testWindowSliding() {
    List<List<Reading>> suspiciousReadings = findSuspicious(Reading.loadRecentReadings());
    assertEquals(1, suspiciousReadings.size());
  }

  private static List<List<Reading>> findSuspicious(Stream<Reading> source) {
    return source.gather(windowSliding(2))
                 .filter(window -> window.size() == 2) // stream with just one element
                 .filter(window -> isSuspicious(window.get(0), window.get(1)))
                 .toList();
  }


  private static boolean isSuspicious(Reading previous, Reading next) {
    // changes of more than 30° Kelvin across two consecutive readings within a five-second window of time
    Duration timeDifference = Duration.between(previous.obtainedAt(), next.obtainedAt());
    int temparatureDifference = Math.absExact(previous.kelvins() - next.kelvins());
    return timeDifference.isPositive() && timeDifference.compareTo(FIVE_SECONDS) <= 0
        && temparatureDifference <= 30;
  }

  record Reading(Instant obtainedAt, int kelvins) {

    Reading(String time, int kelvins) {
      this(Instant.parse(time), kelvins);
    }

    Reading {
      if (kelvins < 0) {
        throw new IllegalArgumentException("kelvins must be positive");
      }
    }

    static Stream<Reading> loadRecentReadings() {
      // In reality these could be read from a file, a database,
      // a service, or otherwise
      return Stream.of(
          new Reading("2023-09-21T10:15:30.00Z", 310),
          new Reading("2023-09-21T10:15:31.00Z", 312),
          new Reading("2023-09-21T10:15:32.00Z", 350),
          new Reading("2023-09-21T10:15:33.00Z", 310)
          );
    }

  }
}
