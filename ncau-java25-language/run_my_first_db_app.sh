#!/bin/sh

JAVA_HOME=/Library/Java/JavaVirtualMachines/25.0.0/Contents/Home
${JAVA_HOME}/bin/java \
  --class-path ~/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar \
  src/main/java/MyFirstDatabaseApplication.java
