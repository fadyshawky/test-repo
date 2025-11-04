# Fix for android.injected.build.density Deprecation Error

## Problem
When building from Android Studio, you may see this error:
```
The option 'android.injected.build.density' is deprecated.
It was removed in version 8.0 of the Android Gradle plugin.
```

## Solution

### Option 1: Build from Command Line (Recommended)
The init script in `gradle/init.gradle` automatically removes the deprecated property when building from command line:

```bash
./gradlew assembleDebug
```

### Option 2: Update Android Studio
Update Android Studio to the latest version (Hedgehog | 2023.1.1 or later) which doesn't inject this deprecated property.

### Option 3: Configure Android Studio
If you must use an older Android Studio version:
1. Go to **File > Settings > Build, Execution, Deployment > Compiler**
2. Remove any `-Pandroid.injected.build.density` from command-line options

### Option 4: Use Init Script Manually
If the init script doesn't run automatically, you can specify it explicitly:
```bash
./gradlew assembleDebug --init-script gradle/init.gradle
```

## Technical Details
- AGP 8.0+ removed support for `android.injected.build.density`
- Android Studio (older versions) still injects this property
- The init script in `gradle/init.gradle` removes this property before AGP applies
- Command-line builds work correctly without this property

