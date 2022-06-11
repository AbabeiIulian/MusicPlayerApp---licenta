package com.licenta2022.musicplayerApp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.licenta2022.musicplayerApp.data.entities.Song
import com.licenta2022.musicplayerApp.other.Constants.SONG_COLLECTION
import kotlinx.coroutines.tasks.await

class MusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()


    //private val songCollection = firestore.collection("/playlists/Playlist1/Songs_in_playlist1")

    suspend fun getSongsByPlaylist(): List<Song>{
        return try{
            //firestore.collection("/playlists/${playlistId}/Songs_in_playlist1").get().await().toObjects(Song::class.java)
            firestore.collection(SONG_COLLECTION).get().await().toObjects(Song::class.java)
        }catch (e: Exception)
        {
            emptyList()
        }
    }

}