# Currency Converter Android App

## Overview

A free Android currency converter app.

A complete, production-ready Android currency converter application built with modern Android development practices. The app fetches real-time exchange rates from the European Central Bank and provides a calculator-style interface for currency conversions.

## Technology Stack

### Language & Framework

- **Kotlin** 1.9.22
- **Jetpack Compose** with Material Design 3
- **Android SDK** 26-34 (Android 8.0 - 14)

### Architecture & Libraries

- **Architecture**: MVVM (Model-View-ViewModel)
- **DI**: Hilt 2.50
- **Database**: Room 2.6.1
- **Preferences**: DataStore 1.0.0
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0
- **Async**: Kotlin Coroutines 1.7.3
- **Navigation**: Compose Navigation 2.7.7

### Build & Tools

- **Gradle**: 8.4
- **Android Gradle Plugin**: 8.3.0
- **KSP**: 1.9.22-1.0.17 (Kotlin Symbol Processing)

## Getting Started

### Quick Start

```bash
# Clone the repository
git clone https://github.com/andreasscherbaum/currency-converter.git
cd currency-converter

# Run setup script (Linux)
./setup.sh

# Build the app
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Development with VS Code

1. Install recommended extensions (prompt appears automatically)
2. Open folder in VS Code
3. Press `Ctrl+Shift+B` to build
4. Use Command Palette → "Tasks: Run Task" for other actions

### Development with Android Studio

1. Open project in Android Studio
2. Wait for Gradle sync
3. Click "Run" button or press `Shift+F10`

## Testing

### Physical Device

```bash
# Enable USB debugging on device
# Connect via USB
adb devices
./gradlew installDebug
```

### Emulator

```bash
# Create AVD
avdmanager create avd -n Test -k "system-images;android-34;google_apis;x86_64" -d pixel_6

# Start emulator
emulator -avd Test

# Install app
./gradlew installDebug
```

### Wireless Debugging (Android 11+)

```bash
# Pair device
adb pair <IP>:<PORT>
adb connect <IP>:<PORT>
./gradlew installDebug
```

See `TESTING.md` for comprehensive testing guide.

## Building for Release

### Unsigned APK (for testing)

```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

### Signed APK (for distribution)

There are currently no plans to distribute this app through any keystore.

I wrote the app for myself. If you are using it, that's fine for me.

## Security Considerations

- HTTPS only for ECB API
- No sensitive data storage
- ProGuard configuration for release builds
- No hardcoded credentials
- Sandboxed app with minimal permissions (only INTERNET)

## Localization

Current languages:
- English (en) - Default
- German (de)

Adding new languages:
1. Create `app/src/main/res/values-<lang>/strings.xml`
2. Translate all strings
3. Add language option in Settings screen

## Data Source

**European Central Bank (ECB)**
- URL: https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
- Format: XML
- Update frequency: Daily around 16:00 CET
- Base currency: EUR
- ~30+ currencies supported

## License

Open source - MIT License

## Contributing

If you have enhancements, consider submitting an issue first before creating a PR.

## Credits

- **Exchange Rates**: European Central Bank
- **Icons**: Material Design Icons
- **Framework**: Jetpack Compose
- **Dependencies**: See `app/build.gradle.kts`
