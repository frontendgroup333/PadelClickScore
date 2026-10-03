# Padel Click Score
Native Android MVP for padel scoring.

Features:
- Padel/tennis scoring: 0/15/30/40, Deuce, Advantage, Game, Sets, 6-6 tie-break, best of 3.
- Optional Golden Point.
- Text-to-Speech score announcement after every point.
- Local Match History using SQLite.
- Undo.
- Screen stays awake during match.
- HID/Bluetooth remote input: single click Team A, double click Team B, long/repeat Undo.
- Phone controls work without any remote.

Build with Android Studio / Gradle using compileSdk 35.

## Cloud build
This package includes GitHub Actions and Codemagic configuration for building an installable debug APK. See `BUILD-ONLINE.md`.
