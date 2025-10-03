package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class StreamGathererTests {

  @Test
  void noTerminalOperation() {
    Stream.of(1, 2, 3).peek(
        i -> assertTrue(i < 0));
  }

}
