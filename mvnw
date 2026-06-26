#!/bin/sh
# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup shell script
# ----------------------------------------------------------------------------

if [ -z "$JAVA_HOME" ]; then
  JAVACMD=java
  if ! command -v java > /dev/null 2>&1; then
    echo "Error: JAVA_HOME is not set and no 'java' command found on PATH." >&2
    exit 1
  fi
else
  JAVACMD="$JAVA_HOME/bin/java"
  if [ ! -x "$JAVACMD" ]; then
    echo "Error: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
    exit 1
  fi
fi

# Prefer globally installed Maven
if command -v mvn > /dev/null 2>&1; then
  exec mvn "$@"
fi

MAVEN_PROJECTBASEDIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

DISTRIBUTION_URL=$(grep "distributionUrl" "$WRAPPER_PROPERTIES" | cut -d= -f2 | tr -d '\r')
MAVEN_DIST_NAME=$(basename "$DISTRIBUTION_URL" .zip)

MAVEN_USER_HOME="${MAVEN_USER_HOME:-$HOME/.m2/wrapper}"
MAVEN_HOME="$MAVEN_USER_HOME/dists/$MAVEN_DIST_NAME"

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  echo "Downloading Maven from: $DISTRIBUTION_URL"
  mkdir -p "$MAVEN_USER_HOME/dists"
  MAVEN_ZIP="$MAVEN_USER_HOME/dists/$MAVEN_DIST_NAME.zip"

  if command -v curl > /dev/null 2>&1; then
    curl -fsSL "$DISTRIBUTION_URL" -o "$MAVEN_ZIP"
  elif command -v wget > /dev/null 2>&1; then
    wget -q "$DISTRIBUTION_URL" -O "$MAVEN_ZIP"
  else
    echo "Error: Neither curl nor wget found. Install one, or place Maven at: $MAVEN_HOME" >&2
    exit 1
  fi

  unzip -q "$MAVEN_ZIP" -d "$MAVEN_USER_HOME/dists"
  rm -f "$MAVEN_ZIP"
  # The zip extracts to a versioned subfolder
  MAVEN_HOME=$(find "$MAVEN_USER_HOME/dists" -maxdepth 1 -name "apache-maven-*" -type d | head -1)
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
