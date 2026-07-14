# Inzila Mapping

**Inzila Mapping** — *Smart Mapping. Better Infrastructure.* A native Android app to map and save infrastructure locations, set geofence boundaries, and get notifications when field teams arrive at or leave a mapped site.

## Overview

Inzila Mapping combines live GPS map tracking with local geofencing. Save named locations with a custom radius, and the app alerts you on entry and exit—even when running in the background. All saved places are stored on-device with Room, so records remain available in the field; no account or backend required.

## Features

- **Live Map Tracking** — Real-time GPS on Google Maps with periodic location updates
- **Saved Locations** — Name and store mapped sites with configurable geofence radii
- **Geofencing Alerts** — Notifications on enter/exit via Google Play Services Location
- **Navigation** — Open turn-by-turn directions to any saved site in Google Maps
- **Offline Storage** — Persistent local database (Room / SQLite)

## Brand & Theme

The app follows the Inzila nature palette drawn from the logo's above/below-ground motif:

| Role | Colour | Hex |
|---|---|---|
| Primary | Forest green | `#2E7D32` |
| Primary dark | Deep forest | `#1B5E20` |
| Accent | Clay orange | `#EF6C00` |
| Earth | Soil brown | `#5D4037` |
| Support | Map pin blue | `#1565C0` |
| Background | Natural off-white | `#F4F7F1` |

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | AndroidX, Material Design 3, Fragments |
| Maps & Location | Google Maps SDK, Google Play Services Location |
| Database | Room (SQLite ORM) |
| Reactive data | LiveData, ViewModel |
| Build | Gradle 8.3.2 |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 34 (Android 14) |

## Project Structure

```
src/
├── .env.example              # API key template (copy to .env locally)
├── app/
│   ├── build.gradle          # Injects MAPS_API_KEY at build time
│   └── src/main/
│       ├── java/com/inzila/mapping/
│       │   ├── MainActivity.java
│       │   ├── fragments/
│       │   │   ├── MapFragment.java
│       │   │   ├── LocationsFragment.java
│       │   │   └── AddLocationFragment.java
│       │   ├── database/
│       │   │   ├── AppDatabase.java
│       │   │   ├── LocationDao.java
│       │   │   └── SavedLocation.java
│       │   ├── adapters/
│       │   │   └── LocationAdapter.java
│       │   └── receivers/
│       │       └── GeofenceBroadcastReceiver.java
│       ├── res/
│       └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 11** or newer
- **Android SDK** with API Level 34 installed
- A **Google Maps API key** with **Maps SDK for Android** enabled ([Google Cloud Console](https://console.cloud.google.com))

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/Nqobileee/indlela-mapping.git
cd indlela-mapping/src
```

### 2. Add your Google Maps API key

API keys are **not** stored in source code. Provide yours via `.env` or `local.properties` (both are gitignored).

**Option A — `.env` (recommended)**

```bash
# macOS / Linux / Git Bash
cp .env.example .env
```

```powershell
# Windows PowerShell
Copy-Item .env.example .env
```

Edit `.env`:

```env
google_maps_api_key=YOUR_API_KEY_HERE
```

**Option B — `local.properties`**

Add to `local.properties` at the project root (same file used for `sdk.dir`):

```properties
MAPS_API_KEY=YOUR_API_KEY_HERE
```

`local.properties` takes precedence over `.env` if both are set.

> **Security:** Never commit `.env`, `local.properties`, or API keys. Restrict your key in Google Cloud (Android app restrictions: package `com.inzila.mapping` + signing certificate SHA-1).

### 3. Android SDK path

Android Studio creates this automatically. If needed, add to `local.properties`:

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

## Build & Run

### Android Studio (recommended)

1. Open the project in Android Studio
2. Connect a device or start an emulator (API 26+)
3. Click **Run** or press `Shift + F10`

### Command line

```bash
# Build a debug APK
./gradlew assembleDebug

# Install on a connected device
./gradlew installDebug

# Build a release APK (requires a signing keystore)
./gradlew assembleRelease
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Permissions

| Permission | Purpose |
|---|---|
| `ACCESS_FINE_LOCATION` | Precise GPS for map tracking and saving sites |
| `ACCESS_COARSE_LOCATION` | Network-based location fallback |
| `ACCESS_BACKGROUND_LOCATION` | Geofence monitoring while the app is in the background |
| `POST_NOTIFICATIONS` | Entry/exit alerts (Android 13+) |

Background location is requested after foreground location is granted, following Android best practices.

## How It Works

### Saving a location

1. Tap **Add** in the bottom navigation bar
2. Tap the map to place a pin, or tap **Use Current Location**
3. Optionally enter latitude/longitude manually
4. Name the place and set the geofence radius (default: 200 m)
5. Tap **Save** — data is stored in Room and a geofence is registered

### Geofence notifications

`GeofenceBroadcastReceiver` handles `GEOFENCE_TRANSITION_ENTER` and `GEOFENCE_TRANSITION_EXIT` from the Google Location Services API:

- **Arrived at [Name]** on entry
- **Left [Name]** on exit

### Navigation

From **Locations**, tap **Navigate** on any saved place to open Google Maps with directions from your current position.

## Database Schema

**Table:** `saved_locations`

| Column | Type | Description |
|---|---|---|
| `id` | INTEGER (PK) | Auto-generated primary key |
| `name` | TEXT | User-defined location name |
| `latitude` | REAL | Decimal degrees (-90 to 90) |
| `longitude` | REAL | Decimal degrees (-180 to 180) |
| `radius` | REAL | Geofence radius in metres |
| `geofenceId` | TEXT | Unique ID for the Google Geofencing API |

## Dependencies

```gradle
// UI
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.recyclerview:recyclerview:1.3.2'

// Maps & Location
implementation 'com.google.android.gms:play-services-maps:18.2.0'
implementation 'com.google.android.gms:play-services-location:21.1.0'

// Room Database
implementation 'androidx.room:room-runtime:2.6.1'
annotationProcessor 'androidx.room:room-compiler:2.6.1'

// Lifecycle
implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
```

## Authors

| Name | Student ID | Email | GitHub |
|---|---|---|---|
| Runhindi Allan Takunda | H240023Z | [runhindialla@gmail.com](mailto:runhindialla@gmail.com) | [@Allan-Runhindi](https://github.com/Allan-Runhindi) |
| Edith Nqobile Muyambiri | H240577T | [nqobilemuyambiri@gmail.com](mailto:nqobilemuyambiri@gmail.com) | [@Nqobileee](https://github.com/Nqobileee) |
| Princess Batsirai Kwaniya | H240301V | [princesskwaniya@gmail.com](mailto:princesskwaniya@gmail.com) | [@Princess-B-Kwaniya](https://github.com/Princess-B-Kwaniya) |

## License

This project is for personal/educational use. See [LICENSE](LICENSE) for details.
