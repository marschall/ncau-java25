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
    System.out.println(ClassLayout.parseClass(Leaf.class).toPrintable());
    /*

JDK 11
------
com.netcetera.ncau.java25.vm.ObjectHeaderTests$Leaf object internals:
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     N/A
  8   4        (object header: class)    N/A
 12   4    int Root.value                N/A
 16   1   byte Root.flags1               N/A
 17   3        (alignment/padding gap)   
 20   1   byte Intermediate.flags2       N/A
 21   3        (alignment/padding gap)   
 24   1   byte Leaf.flags3               N/A
 25   7        (object alignment gap)    
Instance size: 32 bytes
Space losses: 6 bytes internal + 7 bytes external = 13 bytes total

JDK 25 default
--------------
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     N/A
  8   4        (object header: class)    N/A
 12   4    int Root.value                N/A
 16   1   byte Root.flags1               N/A
 17   1   byte Intermediate.flags2       N/A
 18   1   byte Leaf.flags3               N/A
 19   5        (object alignment gap)    
Instance size: 24 bytes
Space losses: 0 bytes internal + 5 bytes external = 5 bytes total


JDK 25 compact object headers
-----------------------------
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     N/A
  8   4    int Root.value                N/A
 12   1   byte Root.flags1               N/A
 13   1   byte Intermediate.flags2       N/A
 14   1   byte Leaf.flags3               N/A
 15   1        (object alignment gap)    
Instance size: 16 bytes
Space losses: 0 bytes internal + 1 bytes external = 1 bytes total

     */
  }

  static class Root {

    int value;

    byte flags1;

  }

  static class Intermediate extends Root {

    byte flags2;

  }

  static class Leaf extends Intermediate {

    byte flags3;

  }

}
