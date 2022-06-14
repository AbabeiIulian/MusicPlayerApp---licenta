
package com.licenta2022.musicplayerApp.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.licenta2022.musicplayerApp.data.entities.Song
import com.licenta2022.musicplayerApp.other.Constants.DB_Playlist
import com.licenta2022.musicplayerApp.other.Constants.SONG_COLLECTION
import kotlinx.coroutines.tasks.await


class MusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()



    suspend fun getSongsByPlaylist(playlistId: String?): List<Song>{
        return try{
            if(playlistId !== null) {
                Log.d("AndroidTag", "getSongsByPlaylist() from music database custom path ")
                firestore.collection(DB_Playlist).document(playlistId).collection(SONG_COLLECTION).get().await().toObjects(Song::class.java)
            } else {
                Log.d("AndroidTag", "getSongsByPlaylist() from music database base path ")

                firestore.collection(SONG_COLLECTION).get().await().toObjects(Song::class.java)
            }

        }catch (e: Exception)
        {
            emptyList()
        }
    }



}

