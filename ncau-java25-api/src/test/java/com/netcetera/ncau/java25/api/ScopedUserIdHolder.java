package com.netcetera.ncau.java25.api;

import java.util.Objects;

public final class ScopedUserIdHolder {

  static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

  public static String getCurrentUserId() {
    return USER_ID.orElseThrow(() -> new IllegalStateException("user id not set"));
  }

  // package scope to allow overriding only from within framework
  static void useDuring(String newUserId, Runnable action) {
    ScopedValue.where(USER_ID, Objects.requireNonNull(newUserId, "newUserId")).run(action);
  }

}
