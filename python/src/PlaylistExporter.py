import spotipy
from spotipy.oauth2 import SpotifyOAuth
import os
import json

os.environ['SPOTIPY_CLIENT_ID'] = '7d80e48c263e48dda9322a9974a26ec7'
os.environ['SPOTIPY_CLIENT_SECRET'] = '8c109a217302409ca73716e855e49692'
os.environ['SPOTIPY_REDIRECT_URI'] = 'https://localhost:8888/callback'

filepath = "../../streaming data/multimap/multimap2.json"
validSongList = []

with open(filepath, 'r') as file:
    data = json.load(file)

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
playlist_id = spotify.user_playlist_create(user=username, name="timemachinetest", public=True, description="take a flight through time")['id']
print('Playlist created')

uris = []
dataLength = len(data)
count = 0
# Add songs to the playlist
for i in range(dataLength):
    # get data of entryset subject
    highest = 0

    for entry in data:
        values = entry["values"]
        uri = entry["key"]
        if uri in uris:
            continue

        # If the condition is met, update 'highest' and 'highest_uri'
        highest = max(values)
        highest_uri = uri

    print("song appended", count, " / ", dataLength)
    uris.append(highest_uri)
    count += 1


# Splitting the 'tracks' list into batches of 100 tracks each
batched_tracks = [uris[i:i + 100] for i in range(0, len(uris), 100)]

# Adding tracks in batches
maxPlaylistSize = 2000
playlistSize = 0
for batch in batched_tracks:
    spotify.playlist_add_items(playlist_id, batch)
    playlistSize += 100
    if playlistSize >= 2000:
        break

print('Songs added to the playlist')


# needs method to create playlist
# needs method to authenticate / set connection
# analyze the json file

