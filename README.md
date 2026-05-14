# Karunada Kote

Karunada Kote is a heritage-focused Android app for exploring Karnataka’s historic forts. It combines a polished dark UI, local fort data, map-based exploration, AI-generated summaries, and narration to create a compact travel and culture showcase.

## Demo APK

Download the demo APK here:

[Google Drive APK](https://drive.google.com/file/d/1e-A6PSv1g8DPjrEnpc8uN_AwLZpwm93o/view?usp=drive_link)

## Features

- Splash screen with animated branding
- Fort list with search and smooth card animations
- Heritage-style UI with cinematic image cards
- Fort detail bottom sheet with:
  - dynasty
  - year built
  - fort type
  - district
  - highlights
  - full description
- Interactive map view using OSMDroid
- Route button that opens Google Maps navigation
- AI-generated historical summary powered by Gemini API
- Text-to-speech narration for fort descriptions
- Visited fort tracking stored locally
- Local fort dataset loaded from `assets/forts.json`
- Responsive empty state, shimmer loading, and modern transitions

## Screenshots

Add screenshots from the app here:

- Home / Fort List
- Fort Detail Bottom Sheet
- AI Summary View
- Map View
- Route in Google Maps

## Tech Stack

- Kotlin
- Android SDK
- MVVM
- ViewBinding
- LiveData
- Retrofit
- Gson
- Coil
- OSMDroid
- Material Components
- Gemini API
- TextToSpeech
- SharedPreferences

## Architecture

The app follows a simple MVVM structure:

- `ui/` — activities, adapters, and bottom sheets
- `viewmodel/` — app state and actions
- `data/` — repository, local storage, and models
- `network/` — Gemini API client and response models
- `assets/forts.json` — fort data source

## Setup

### Prerequisites
- Android Studio
- JDK 17
- Android device or emulator
- Internet connection for AI summaries and maps

### Run locally
1. Clone the repository
2. Open the project in Android Studio
3. Add your Gemini API key to `local.properties`:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```
4. Sync Gradle
5. Run the app on a device or emulator

## Project Details

- `minSdk`: 26
- `targetSdk`: 34
- Package name: `com.karunadakote`

## Notes

- AI summaries require a valid Gemini API key.
- Map routes open in Google Maps.
- Visited status is saved locally on the device.

## Repository

GitHub:
https://github.com/vishwanath090/Karunada-Kote
