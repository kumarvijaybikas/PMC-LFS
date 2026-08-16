#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Start Up Shell Script
# ----------------------------------------------------------------------------

export MAVEN_PROJECTBASEDIR="${MAVEN_PROJECTBASEDIR:-$(pwd)}"
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

JAVACMD="java"
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
fi

exec "$JAVACMD" \
  "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
  -classpath "$WRAPPER_JAR" \
  $WRAPPER_LAUNCHER "$@"
