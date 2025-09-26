package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;

import org.junit.jupiter.api.Test;

class WithoutFlexibleConstructorBodiesTests {
  
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
      super(age);  // Potentially unnecessary work
      if (age < 18 || age > 65) {
        throw new IllegalArgumentException("not of employment age: " + age);
      }
      this.officeId = Objects.requireNonNull(officeId, "officeId");
    }

    @Override
    void show() {
      super.show();
      System.out.println("OfficeId: " + this.officeId);
    }

  }

}
