# Online APK Build

This project is prepared for cloud builds.

## GitHub Actions (recommended)
1. Create a GitHub repository and upload the CONTENTS of this folder to the repository root.
2. Open the repository's **Actions** tab.
3. Choose **Build Android APK**.
4. Click **Run workflow**.
5. When it finishes, open the workflow run and download the artifact named **PadelClickScore-debug-apk**.
6. Extract the artifact ZIP; inside it is `app-debug.apk`.

## Codemagic
1. Import the GitHub repository into Codemagic.
2. Codemagic detects `codemagic.yaml`.
3. Run workflow **PadelClickScore Debug APK**.
4. Download the APK from build artifacts.

## Local/other CI
Run:

    ./gradlew assembleDebug

APK output:

    app/build/outputs/apk/debug/app-debug.apk

Note: `gradlew` is a lightweight bootstrap script in this package. It downloads Gradle 8.9 automatically on first run, so the standard Gradle wrapper JAR is not required.
