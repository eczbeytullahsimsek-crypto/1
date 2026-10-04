#!/usr/bin/env sh
# Small self-bootstrapping Gradle launcher. The standard wrapper JAR is intentionally
# not checked in; this script downloads the pinned distribution when needed.
set -eu

GRADLE_VERSION="8.9"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
DIST_DIR="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-bin/meva"
GRADLE_BIN="$DIST_DIR/gradle-${GRADLE_VERSION}/bin/gradle"

if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

if [ ! -x "$GRADLE_BIN" ]; then
    if ! command -v curl >/dev/null 2>&1 || ! command -v unzip >/dev/null 2>&1; then
        echo "Gradle $GRADLE_VERSION is not installed. Install Gradle, or install curl and unzip so this launcher can download it." >&2
        exit 1
    fi
    mkdir -p "$DIST_DIR"
    ARCHIVE="$DIST_DIR/gradle-${GRADLE_VERSION}-bin.zip"
    echo "Downloading Gradle $GRADLE_VERSION…"
    curl -fL --retry 3 "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "$ARCHIVE"
    unzip -q -o "$ARCHIVE" -d "$DIST_DIR"
    rm -f "$ARCHIVE"
fi

exec "$GRADLE_BIN" "$@"
