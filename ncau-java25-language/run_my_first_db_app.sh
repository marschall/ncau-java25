#!/bin/sh

JAVA_HOME=/Library/Java/JavaVirtualMachines/25.0.0/Contents/Home
${JAVA_HOME}/bin/java \
  --class-path ~/.m2/repository/com/h2database/h2/2.4.240/h2-2.4.240.jar \
  src/main/java/MyFirstDatabaseApplication.java
