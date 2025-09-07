package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

class SecurityManagerTests {

  @Test
  void setSecurityManager() {
    assertThrows(UnsupportedOperationException.class, () -> System.setSecurityManager(new SecurityManager()));
  }
  
  void doP() {
    AtomicBoolean called = new AtomicBoolean();
    assertTrue(AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
      called.set(true);
      return true;
    }));
    assertTrue(called.get());
  }

}
