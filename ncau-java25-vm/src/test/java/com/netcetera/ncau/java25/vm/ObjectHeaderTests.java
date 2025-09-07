package com.netcetera.ncau.java25.vm;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;

/**
 * Demonstrates the new compact object headers.
 * 
 * @see <a href="https://openjdk.org/jeps/450" title="JEP 450">JEP 450: Compact Object Headers (Experimental)</a>
 * @see <a href="https://openjdk.org/jeps/519" title="JEP 519">JEP 519: Compact Object Headers</a>
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8237767" title="JDK-8237767">Field layout computation overhaul</a>
 */
class ObjectHeaderTests {
  
  /**
   * Run in Java 25 with
   * -XX:+UseCompactObjectHeaders
   * Run in Java 11 for comparison
   */
  @Test
  void compactHeaderAndNewLayouter() {
    System.out.println(ClassLayout.parseClass(Child.class).toPrintable());
    /*

OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     N/A
  8   4    int Parent.parentField        N/A
 12   4    int Child.childField          N/A
 
     */
  }

  static class Parent {

    int parentField;

  }

  static class Child extends Parent {

    int childField;

  }

}
