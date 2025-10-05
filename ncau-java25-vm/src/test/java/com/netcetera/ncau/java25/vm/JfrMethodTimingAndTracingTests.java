package com.netcetera.ncau.java25.vm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

public class JfrMethodTimingAndTracingTests {

  // -XX:StartFlightRecording:jdk.MethodTrace#filter=java.nio.ByteBuffer::allocate,filename=unit-tests.jfr
  @Test
  void invokeTracedMethod() {
    int capacity = 1024;
    var buffer = ByteBuffer.allocate(capacity);
    assertNotNull(buffer);
    assertEquals(capacity, buffer.capacity(), "capacity");
  }

}
