package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReaderTests {

  private Reader reader;

  @BeforeEach
  void setUp() {
    this.reader = Reader.of("hello\nworld");
  }

  @Test
  void readAllAsString() throws IOException {
    assertEquals("hello\nworld", this.reader.readAllAsString());
  }
  
  @Test
  void readAllLines() throws IOException {
    assertEquals(List.of("hello", "world"), this.reader.readAllLines());
  }
  
}
