package com.netcetera.ncau.java25.api;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jdk.jfr.Category;
import jdk.jfr.Contextual;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Throttle;

/**
 * JDK 25 Flight Recorder examples
 * 
 * Demonstrates:
 * <ul>
 *  <li>event throttling</li>
 *  <li>contextual events</li>
 * </ul>
 * 
 * <pre><code>
 * /Library/Java/JavaVirtualMachines/25.0.1/Contents/Home/bin/jfr print --events "example.PostMessage" ncau-java25-api/target/unit-tests.jfr
 * </code></pre>
 */
class JfrTests {
  
  private long lastTransactionId;

  @BeforeEach
  void setUp() {
    this.lastTransactionId = 0L;
  }

  @Test
  void events() {
    for (int i = 0; i < 2; i++) {
      this.processNewTransaction();
    }
  }
  
  private void processNewTransaction() {
    this.processNewTransaction(++this.lastTransactionId);
  }

  private void processNewTransaction(long transactionId) {
    var transactionEvent = new TransactionEvent();
    transactionEvent.transactionId = transactionId;
    transactionEvent.begin();

    List<String> messages = new ArrayList<>();
    Channel channel = messages::add;
    
    this.postMessage(channel, "authorize");
    this.postMessage(channel, "clear");
    this.postMessage(channel, "report");
    
    assertEquals(List.of("authorize", "clear", "report"), messages);
    
    transactionEvent.commit();
  }

  private void postMessage(Channel channel, String message) {
    channel.publish(message);
    var event = new PostMessageEvent();
    // should commit for rate limiting
    if (event.shouldCommit()) {
      if (message.length() < 26) {
        event.message = message;
      } else {
        event.message = message.substring(0, 22) + "...";
      }
      event.commit();
    }
  }

  @Throttle("5/s")
  @Label("Post Message")
  @Name("example.PostMessage")
  @Category("NCAU Java 25")
  static class PostMessageEvent extends Event {
    @Label("Message")
    String message; 
  }
  
  @Label("Process Transaction")
  @Name("example.Transaction")
  @Category("NCAU Java 25")
  static class TransactionEvent extends Event {
    
    @Contextual
    @Label("Transaction Id")
    long transactionId; 
  }

  interface Channel {

    void publish(String message);

  }

}
