package com.netcetera.ncau.java25.langauge;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;

import org.junit.jupiter.api.Test;

class WithFlexibleConstructorBodiesTests {

  @Test
  void validEmployee() {
    var employee = new Employee(23, "ZRH");
    assertNotNull(employee);
  }

  static class Person {

    private int age;

    Person(int age) {
      if (age < 0) {
        throw new IllegalArgumentException("negative age");
      }
      this.age = age;
      this.show();
    }

    void show() {
      System.out.println("Age: " + this.age);
    }

  }

  class Employee extends Person {

    private final String officeId;

    Employee(int age, String officeId) {
      // see https://bugs.openjdk.org/browse/JDK-8233268
      this.officeId = Objects.requireNonNull(officeId, "officeId");
      if (age < 18 || age > 65) {
        throw new IllegalArgumentException("not of employment age: " + age);
      }
      super(age);
    }

    @Override
    void show() {
      super.show();
      System.out.println("OfficeId: " + this.officeId);
    }

  }

}
