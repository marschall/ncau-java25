package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class VirtualMachineImpl2Tests {

  @Test
  void checkCatchesAndSendQuitTo() throws IOException {
    Path statusParent = Path.of("target", "test-classes", "status").getParent();
    boolean result = VirtualMachineImpl2.checkCatchesAndSendQuitTo(statusParent);
    assertTrue(result);
  }

}
