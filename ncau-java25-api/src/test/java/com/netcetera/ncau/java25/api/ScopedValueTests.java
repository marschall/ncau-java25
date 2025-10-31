package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ScopedValueTests {

  @Test
  void withScopedValues() {
    assertThrows(IllegalStateException.class, ScopedUserIdHolder::getCurrentUserId);

    ScopedUserIdHolder.useDuring("user1", () -> {
      assertEquals("user1", ScopedUserIdHolder.getCurrentUserId());

      ScopedUserIdHolder.useDuring("user2", () -> {
        assertEquals("user2", ScopedUserIdHolder.getCurrentUserId());
      });

      assertEquals("user1", ScopedUserIdHolder.getCurrentUserId());
    });

    assertThrows(IllegalStateException.class, ScopedUserIdHolder::getCurrentUserId);
  }

  @Test
  void withThreadLocals() {
    assertThrows(IllegalStateException.class, ThreadLocalUserIdHolder::getCurrentUserId);

    ThreadLocalUserIdHolder.useDuring("user1", () -> {
      assertEquals("user1", ThreadLocalUserIdHolder.getCurrentUserId());

      ThreadLocalUserIdHolder.useDuring("user2", () -> {
        assertEquals("user2", ThreadLocalUserIdHolder.getCurrentUserId());
      });

      assertEquals("user1", ThreadLocalUserIdHolder.getCurrentUserId());
    });

    assertThrows(IllegalStateException.class, ThreadLocalUserIdHolder::getCurrentUserId);
  }

}
