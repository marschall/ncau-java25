package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.IOException;
import java.nio.CharBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CharBufferTests {

  private CharBuffer buffer;

  @BeforeEach
  void setUp() {
    this.buffer = CharBuffer.wrap("hello");
  }

  @Test
  void getChars() throws IOException {
    char[] array = new char[5];
    this.buffer.getChars(0, 5, array, 0);
    assertArrayEquals("hello".toCharArray(), array);
  }
  
  
}
