#!/bin/sh
#
# Gradle start up script for POSIX systems (including Mac OS X)
#

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
app_path=$0
while
  APP_HOME=${app_path%"${app_path##*/}"}
  [ -h "$app_path" ]
do
  ls=$( ls -ld "$app_path" )
  link=${ls#*' -> '}
  case $link in
    /*)   app_path=$link ;;
    * )   app_path=$APP_HOME$link ;;
  esac
done
APP_HOME=$( cd "${APP_HOME:-./}" && pwd -P ) || exit

APP_NAME="Gradle"
APP_BASE_NAME=${0##*/}

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

JAVA_EXE="java"
if ! command -v "$JAVA_EXE" > /dev/null 2>&1; then
  echo "ERROR: JAVA_HOME is not set" >&2
  exit 1
fi

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec "$JAVA_EXE" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
  "-Dorg.gradle.appname=$APP_BASE_NAME" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
