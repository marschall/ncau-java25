#!/bin/sh

JAVA_HOME=/Users/pmarscha/Documents/dev/java/zulu25.0.47-beta-jdk25.0.0-beta.34-macosx_aarch64/zulu-25.jdk/Contents/Home
${JAVA_HOME}/bin/java \
  --class-path ~/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar \
  src/main/java/com/netcetera/ncau/java25/langauge/MyFirstDatabaseApplication.java
