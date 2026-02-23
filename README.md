# SkyExplorer

SkyExplorer is an Android application that allows users to explore the night sky. It provides a real-time sky map, detailed information about constellations, and a personal photo gallery for your astronomical observations.

## Features

*   **Sky Map:** A real-time, interactive map of the sky showing stars, planets, and constellations.
*   **Constellation Guide:** Detailed information about various constellations, including their stars, mythology, and visibility.
*   **Photo Gallery:** A personal gallery to store and view your astrophotography.
*   **Camera Integration:** Capture photos of the night sky directly from the app.

## Project Structure

The project is structured as a standard Android application with a single `:app` module. The codebase is written in Kotlin and follows modern Android development practices.

*   `app/src/main/java/com/example/skyexplorer/`: Main application source code.
    *   `MainActivity.kt`: The main entry point of the application.
    *   `SkyExplorerApp.kt`: The main application class.
    *   `components/`: Reusable UI components.
    *   `constellations/`: Features related to constellations.
    *   `data/`: Data models and sources.
    *   `database/`: Room database for local data storage.
    *   `photoGallery/`: The photo gallery feature.
    *   `skymapscreen/`: The sky map feature.
    *   `ui/`: UI-related classes, such as themes and colors.

## Dependencies

The project uses several popular Android libraries, including:

*   **Jetpack Compose:** For building the user interface.
*   **Room:** For local data storage.
*   **CameraX:** For camera integration.
*   **Navigation Component:** For in-app navigation.

## Building the Project

1.  Clone the repository: `git clone https://github.com/your-username/SkyExplorer.git`
2.  Open the project in Android Studio.
3.  Build and run the application.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
