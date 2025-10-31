package com.netcetera.ncau.java25.api;

import java.util.Objects;

public final class ThreadLocalUserIdHolder {

  static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

  public static String getCurrentUserId() {
    String userId = USER_ID.get();
    if (userId == null) {
      throw new IllegalStateException("userId not set");
    }
    return userId;
  }

  // package scope to allow overriding only from within framework
  static void useDuring(String newUserId, Runnable action) {
    Objects.requireNonNull(newUserId, "newUserId");
    String previous = USER_ID.get();
    USER_ID.set(newUserId);
    try {
      action.run();
    } finally {
      if (previous != null) {
        USER_ID.set(previous);
      } else {
        USER_ID.remove();
      }
    }
  }


}
