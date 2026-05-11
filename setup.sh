#!/bin/bash

# Currency Converter App - Linux Setup Script

set -e

echo "================================"
echo "Currency Converter App Setup"
echo "================================"
echo ""

# Check Java
echo "Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "Java not found. Installing OpenJDK 17..."
    sudo apt-get update
    sudo apt-get install -y openjdk-17-jdk
else
    echo "Java found: $(java -version 2>&1 | head -n 1)"
fi

# Set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
    echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64" >> ~/.bashrc
    echo "JAVA_HOME set to $JAVA_HOME"
fi

# Check Android SDK
echo ""
echo "Checking Android SDK..."
if [ -z "$ANDROID_HOME" ]; then
    echo "ANDROID_HOME not set."
    echo "Please install Android SDK manually:"
    echo "  1. Download from: https://developer.android.com/studio#command-tools"
    echo "  2. Extract to ~/Android/Sdk"
    echo "  3. Add to ~/.bashrc:"
    echo "     export ANDROID_HOME=\$HOME/Android/Sdk"
    echo "     export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin"
    echo "     export PATH=\$PATH:\$ANDROID_HOME/platform-tools"
    echo ""
    echo "Or install Android Studio which includes the SDK."
else
    echo "ANDROID_HOME found: $ANDROID_HOME"
fi

# Make gradlew executable
echo ""
echo "Setting up Gradle wrapper..."
chmod +x gradlew
echo "Gradle wrapper is executable"

# Check ADB
echo ""
echo "Checking ADB..."
if command -v adb &> /dev/null; then
    echo "ADB found: $(adb --version | head -n 1)"

    # Check for connected devices
    echo ""
    echo "Checking for connected devices..."
    DEVICES=$(adb devices | grep -v "List" | grep "device" | wc -l)
    if [ $DEVICES -gt 0 ]; then
        echo "Found $DEVICES connected device(s)"
        adb devices
    else
        echo "No devices connected"
        echo "Connect a device or start an emulator to test the app"
    fi
else
    echo "ADB not found. Install Android SDK platform-tools"
fi

echo ""
echo "================================"
echo "Setup Complete!"
echo "================================"
echo ""
echo "Next steps:"
echo "  1. Build the app:     ./gradlew assembleDebug"
echo "  2. Install on device: ./gradlew installDebug"
echo "  3. Run tests:         ./gradlew test"
echo "  4. Run lint:          ./gradlew lint"
echo ""
echo "For VS Code:"
echo "  - Press Ctrl+Shift+B to build"
echo "  - Use Command Palette → Tasks: Run Task"
echo ""
echo "For more details, see README.md and TESTING.md"
echo ""
