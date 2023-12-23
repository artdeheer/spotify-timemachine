import spotipy
from spotipy.oauth2 import SpotifyOAuth
import os

os.environ['SPOTIPY_CLIENT_ID'] = '7d80e48c263e48dda9322a9974a26ec7'
os.environ['SPOTIPY_CLIENT_SECRET'] = '8c109a217302409ca73716e855e49692'
os.environ['SPOTIPY_REDIRECT_URI'] = 'https://localhost:8888/callback'

filepath = "../../streaming data/valid songs/valid_songs.txt"
validSongList = []

with open(filepath, 'r') as file:
    for line in file:
        validSongList.append(line)

print(validSongList)

# Your Spotify username and the playlist ID you want to add songs to
username = "artfromkunst"
# playlist_id = "4AQxUmzPmSuOWvFCfU4YyL"

# Authenticate and authorize the Spotipy client
scope = "playlist-modify-public playlist-modify-private"
token = SpotifyOAuth(scope=scope, username=username)
spotify = spotipy.Spotify(auth_manager=token)

print('Authentication successful')

# Creating a new playlist
playlist_id = spotify.user_playlist_create(user=username, name="timemachine", public=True, description="take a flight through time")['id']
print('Playlist created')

# Add songs to the playlist
temp = []
for song in validSongList:
    song_uri = song.strip()  # Remove newline characters and any potential whitespace
    if song_uri.startswith("spotify:track:"):  # Validate if the URI format is correct
        temp.append(song_uri)
        print(f"Added: {song_uri}")
    else:
        print(f"Ignored: {song_uri} (Invalid Spotify URI format)")

# Print the list of songs being added before attempting to add them to the playlist
print("Songs to be added to the playlist:")
print(temp)

# Splitting the 'tracks' list into batches of 100 tracks each
batched_tracks = [temp[i:i + 100] for i in range(0, len(temp), 100)]

# Adding tracks in batches
for batch in batched_tracks:
    spotify.playlist_add_items(playlist_id, batch)

# spotify.playlist_add_items(playlist_id, temp)  # Add the validated song URIs to the playlist

print('Songs added to the playlist')


