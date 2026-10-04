# Meva Notes (Android / Kotlin)

The supplied Meva Notes screens have been rebuilt as a native Android app with Kotlin and Jetpack Compose. The app opens on the welcome screen and includes the collection library, the notebook/drawing canvas, the folder and tag flows, and the main popups/actions from the HTML reference.

## Run

1. Open this repository in Android Studio Ladybug (2024.2.1) or newer.
2. Use JDK 17 and install Android SDK 35.
3. Sync the Gradle project, then run the `app` configuration on an emulator or Android device.

From a terminal, `./gradlew :app:assembleDebug` runs the build. The launcher downloads the pinned Gradle 8.9 distribution if it is not already available. (Android SDK and JDK 17 are still required.)

## Included

- Native Compose welcome screen with the supplied leaf/page Meva mark.
- Searchable, sortable, filterable collection library with responsive folder cards, tags, archive/trash cards, and a navigation drawer.
- Folder/tag creation, quick-note editor, statistics, shortcuts, share/export, and trash confirmation dialogs.
- A notebook screen with pen/highlighter/eraser tools, color and stroke-width selection, undo/redo, zoom, draggable pastel notes, and a touch-drawing surface.
- Quick notes, custom folders, custom tags, and archive/trash counts stored locally with Android SharedPreferences.
- Markdown and PDF exports shared through Android's native share sheet.

The interface and visible sample data are intentionally in Turkish, following the reference. No network connection is needed for the app UI.
