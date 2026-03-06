#!/bin/sh

#
# Copyright © 2015-2021 the original authors.
# Licensed under the Apache License, Version 2.0
#

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
APP_HOME=`dirname "$0"`
APP_HOME=`cd "$APP_HOME" && pwd`

DEFAULT_JVM_OPTS=""
JAVA_EXE="java"

# Check java
if ! command -v "$JAVA_EXE" > /dev/null 2>&1; then
    echo "ERROR: JAVA_HOME is not set." >&2
    exit 1
fi

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec "$JAVA_EXE" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
    "-Dorg.gradle.appname=$APP_BASE_NAME" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
