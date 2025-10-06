package com.netcetera.ncau.java25.api;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;

import org.junit.jupiter.api.Test;

class DeflaterTests {

  @Test
  void deflate() {
    byte[] input = "blahblahblah\u20AC\u20AC".getBytes(UTF_8);

    // Compress the bytes
    ByteArrayOutputStream compressedBaos = new ByteArrayOutputStream();
    try (Deflater compressor = new Deflater()) {
      compressor.setInput(input);
      // Let the compressor know that the complete input has been made available
      compressor.finish();
      // Keep compressing the input till the compressor is finished compressing
      // Use some reasonable size for the temporary buffer based on the data being compressed
      byte[] buffer = new byte[32];
      while (!compressor.finished()) {
        int numCompressed = compressor.deflate(buffer);
        // Copy over the compressed bytes from the temporary buffer into the final byte array
        compressedBaos.write(buffer, 0, numCompressed);
      }
    }
  }

}
