package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.Security;
import java.util.Set;

import org.junit.jupiter.api.Test;

class KdfTests {
  
  @Test
  void availbleAlgorithms() {
    Set<String> algorithms = Security.getAlgorithms("KDF");
    assertEquals(Set.of("HKDF-SHA512", "HKDF-SHA256", "HKDF-SHA384"), algorithms);
  }

}
