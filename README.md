# Trackify

A native Android location tracking app that lets you save places, set geofences, and receive notifications when you arrive at or leave a saved location.

## Features

- **Live Map Tracking** — Real-time GPS display on an interactive Google Map with 5-second location updates
- **Saved Locations** — Store custom locations with names and configurable geofence radii
- **Geofencing Alerts** — Automatic push notifications when you enter or leave a saved location boundary
- **Navigation** — Open turn-by-turn directions to any saved location via Google Maps
- **Offline Storage** — All saved locations persist locally using a Room database

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
trackify/
├── app/src/main/
│   ├── java/com/trackify/app/
│   │   ├── MainActivity.java               # Entry point, bottom navigation controller
│   │   ├── fragments/
│   │   │   ├── MapFragment.java            # Live map, current location, directions
│   │   │   ├── LocationsFragment.java      # List of saved locations
│   │   │   └── AddLocationFragment.java    # Location picker + geofence setup
│   │   ├── database/
│   │   │   ├── AppDatabase.java            # Room database singleton
│   │   │   ├── LocationDao.java            # DAO (CRUD operations)
│   │   │   └── SavedLocation.java          # Entity model
│   │   ├── adapters/
│   │   │   └── LocationAdapter.java        # RecyclerView adapter
│   │   └── receivers/
│   │       └── GeofenceBroadcastReceiver.java  # Handles geofence entry/exit events
│   ├── res/                                # Layouts, drawables, values, menus
│   └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 11** or newer
- **Android SDK** with API Level 34 installed
- A **Google Maps API key** with the following APIs enabled:
  - Maps SDK for Android
  - Geolocation API

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/Edith-Nqobile/trackify.git
cd trackify
```

### 2. Configure your Google Maps API key

Open [app/src/main/AndroidManifest.xml](app/src/main/AndroidManifest.xml) and replace the placeholder value:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
```

Alternatively, set it via a `.env` file at the project root:

```env
google_maps_api_key=YOUR_API_KEY_HERE
```

### 3. Set your local Android SDK path

Create `local.properties` at the project root (this file is not committed):

```properties
sdk.dir=/path/to/your/Android/Sdk
```

Android Studio does this automatically when you open the project.

## Build & Run

### Android Studio (recommended)

1. Open the project in Android Studio
2. Connect a physical device or start an emulator (API 26+)
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

## Permissions

The app requests the following permissions at runtime:

| Permission | Purpose |
|---|---|
| `ACCESS_FINE_LOCATION` | Precise GPS for map tracking and location saving |
| `ACCESS_COARSE_LOCATION` | Fallback network-based location |
| `ACCESS_BACKGROUND_LOCATION` | Geofence monitoring while app is in the background |
| `POST_NOTIFICATIONS` | Geofence entry/exit alerts (Android 13+) |

Background location is requested separately after foreground location is granted, following Android best practices.

## How It Works

### Saving a Location
1. Tap **Add** in the bottom navigation bar
2. Tap anywhere on the map to place a pin, or tap **Use Current Location**
3. Optionally enter latitude/longitude manually
4. Give the location a name and set the geofence radius using the slider (default: 200 m)
5. Tap **Save** — the location is stored in the local Room database and a geofence is registered

### Geofence Notifications
`GeofenceBroadcastReceiver` listens for `GEOFENCE_TRANSITION_ENTER` and `GEOFENCE_TRANSITION_EXIT` events from the Google Location Services API and fires a high-priority notification:
- **Arrived at [Name]** on entry
- **Left [Name]** on exit

### Navigation
From the **Locations** list, tap **Navigate** on any saved location to open Google Maps with directions pre-filled from your current position.

## Database Schema

**Table:** `saved_locations`

| Column | Type | Description |
|---|---|---|
| `id` | INTEGER (PK) | Auto-generated primary key |
| `name` | TEXT | User-defined location name |
| `latitude` | REAL | Decimal degrees (-90 to 90) |
| `longitude` | REAL | Decimal degrees (-180 to 180) |
| `radius` | REAL | Geofence radius in metres |
| `geofenceId` | TEXT | Unique ID used with Google Geofencing API |

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
