# Music Player App

## Overview
This is a simple music player app for Android that allows users to browse and play music tracks. It features background playback, seek functionality, and smooth transitions between activities while maintaining playback state.

## Features
- Play, pause, and seek within songs
- Background playback using a bound `MusicService`
- Persistent playback state when switching between `MainActivity` and `PlayerActivity`
- Seekbar synchronization with playback
- Handles song selection efficiently to prevent unnecessary restarts

## Project Structure
```
com.rixosys.musicplayer
│── adapters/
│   ├── MusicAdapter.kt
│── service/
│   ├── MusicService.kt
│── viewmodels/
│   ├── MusicViewModel.kt
│── 
│   ├── MainActivity.kt
│   ├── PlayerActivity.kt
│── model/
│   ├── Song.kt
│── res/
│   ├── layout/
│   ├── drawable/
│   ├── values/
│── AndroidManifest.xml
│── build.gradle
```

## Installation
1. Clone the repository:
   ```sh
   [git clone https://github.com/spradhan7656/music-app.git ]
   ```
2. Open the project in Android Studio.
3. Build and run the application on an emulator or physical device.

## How It Works
- `MainActivity` lists available songs.
- Selecting a song opens `PlayerActivity` and starts playback.
- If the selected song is already playing, `PlayerActivity` is brought to the front instead of restarting playback.
- The `MusicService` handles background playback and maintains the current song index and playback position.

## Fixes & Improvements
- Prevents restarting playback when reselecting the currently playing song.
- Seekbar updates correctly when returning to `PlayerActivity`.
- Background service efficiently manages playback state and avoids redundant calls.

## Contributing
Pull requests are welcome! If you find any issues, please open an issue or contribute a fix.

## License
This project is licensed under the MIT License. See `LICENSE` for details.

