# Spotify Timemachine

Spotify Timemachine analyzes a user's Spotify streaming history to identify tracks that defined specific time periods and rebuilds them into playlists automatically. The project combines Java for data analysis and Python for automation and playlist creation via the Spotify API.

## Overview

1. **Data Analysis (Java)**  
   The `HistoryAnalyzer.java` script parses Spotify streaming history JSON files, filters valid tracks, and generates a list of consistently played songs.  
   - Input: Spotify streaming history JSON files (from Spotify data export).  
   - Output: `valid_songs.txt` containing track URIs.

2. **Automation and Integration (Python)**  
   The Python scripts handle communication between components and with Spotify’s Web API.  
   - `PlaylistExporter.py` reads `valid_songs.txt` and creates a new Spotify playlist.  
   - `TerminalCommandRunner.py` and `Main.py` compile and run the Java analyzer automatically.

## File Structure

```
.
├── java/
│   ├── HistoryAnalyzer.java       # Main data analysis logic
│   └── TerminalCommandRunner.java # Runs shell commands from Java
├── python/
│   ├── Main.py                    # Orchestrates Java compilation/execution
│   ├── TerminalCommandRunner.py   # Runs shell/Java commands from Python
│   └── PlaylistExporter.py        # Creates playlists using Spotify API
└── streaming data/
    ├── rawdata/                   # Place your Spotify JSON files here
    └── valid songs/               # Output folder for valid_songs.txt
```

## Setup and Usage

1. **Install dependencies**
   ```bash
   pip install spotipy python-dotenv
   ```

2. **Set up environment variables**  
   Create a `.env` file in the project root:
   ```
   SPOTIPY_CLIENT_ID=your_client_id
   SPOTIPY_CLIENT_SECRET=your_client_secret
   SPOTIPY_REDIRECT_URI=https://localhost:8888/callback
   ```

3. **Place your Spotify data**  
   Add your streaming history JSON files to `streaming data/rawdata/`.

4. **Run the analyzer and exporter**
   ```bash
   python Main.py
   python PlaylistExporter.py
   ```

## Output

- `valid_songs.txt` — a text file containing Spotify track URIs that meet the playback frequency threshold.  
- Automatically generated Spotify playlist named *“timemachine”* under your account.

## Notes

- The analysis threshold and time window can be adjusted in `HistoryAnalyzer.java`.  
- Ensure your `.env` file is included in `.gitignore` to protect credentials.  
- Java must be installed and accessible from your system’s PATH.