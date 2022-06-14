package com.licenta2022.musicplayerApp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.licenta2022.musicplayerApp.adapters.PlaylistAdapter
import com.licenta2022.musicplayerApp.data.entities.Playlist
import com.licenta2022.musicplayerApp.data.entities.Song
import com.licenta2022.musicplayerApp.data.remote.MusicDatabase
import com.licenta2022.musicplayerApp.exoplayer.FirebaseMusicSource
import com.licenta2022.musicplayerApp.other.Constants
import com.licenta2022.musicplayerApp.other.Constants.DB_Playlist
import com.licenta2022.musicplayerApp.other.Constants.NEW_SONG
import com.licenta2022.musicplayerApp.other.Constants.SONG_COLLECTION
import com.licenta2022.musicplayerApp.ui.MainActivity
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.playlist_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class Playlist_activity : AppCompatActivity(), PlaylistAdapter.OnPlaylistClickListener {
    private lateinit var playListRecyclerView: RecyclerView
    //private lateinit var newSong: Song
    lateinit var adapter: PlaylistAdapter
    lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        auth = FirebaseAuth.getInstance()

        setupRecyclerView()

        getSongFromIntent()



        createNewPlaylist.setOnClickListener{
            showdialog()
        }


    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    private fun setupRecyclerView() {


        val query : Query = firestore
            .collection(DB_Playlist).whereEqualTo("userID", auth.currentUser?.uid.toString())

        query.addSnapshotListener { value, error ->
            if (value !== null) {
                val playlists: List<Playlist> = value.toObjects(Playlist::class.java)
            }
        }


        val options: FirestoreRecyclerOptions<Playlist> = FirestoreRecyclerOptions.Builder<Playlist>()
            .setQuery(query, Playlist::class.java)
            .build()

         adapter = PlaylistAdapter(this, options)


        playListRecyclerView = findViewById(R.id.playlistRecyclerID)



        playListRecyclerView.adapter = adapter
    }

    override fun onPlaylistClickListener(playlist: Playlist, playlistId: String) {
        Toast.makeText(applicationContext, playlist.playlistName, Toast.LENGTH_SHORT).show()
        Log.d("AndroidTag playlistID", playlistId)

        val songFromIntent = getSongFromIntent()

        if(songFromIntent !== null) {
        //adauga in playlist
            addSongToPlaylist(songFromIntent, playlistId)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        } else {
        //intra in playlist
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("playlistId", playlistId)

            startActivity(intent)
            //finish()
        }

    }

    fun getSongFromIntent() : Song? {
        if (intent.extras !== null) {
            var newSong: Song
            newSong = intent.getSerializableExtra(NEW_SONG) as Song
            Toast.makeText(applicationContext, newSong.title, Toast.LENGTH_LONG).show()
            return newSong
    }
return null
    }

    private fun removePlaylist(){
        val db : FirebaseFirestore =  FirebaseFirestore.getInstance()

        db.collection(DB_Playlist).document()
            .delete()
            .addOnSuccessListener { Log.d("AndroidTag", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("AndroidTag", "Error deleting document", e) }
    }


    private fun showdialog(){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Title")

// Set up the input
        val input = EditText(this)
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Enter the new playlist title")
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

// Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            var playlistName = input.text.toString()
            var userID = auth.currentUser?.uid.toString()
            addPlaylistToDB(playlistName, userID)
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    private fun addPlaylistToDB(plName: String, userUID: String){

        val playlist = hashMapOf(
            "playlistName" to plName,
            "userID" to userUID
        )

       firestore.collection(DB_Playlist).document().set(playlist)
           .addOnSuccessListener { Log.d(Constants.ANDROID_TAG, "DocumentSnapshot successfully written!") }
           .addOnFailureListener { e -> Log.w(Constants.ANDROID_TAG, "Error writing document", e) }


    }

    private fun addSongToPlaylist(song: Song, playlistId: String){
        firestore.collection(DB_Playlist).document(playlistId).collection(SONG_COLLECTION).document().set(song)
            .addOnSuccessListener { Log.d(Constants.ANDROID_TAG, "Song successfully written!") }
            .addOnFailureListener { e -> Log.w(Constants.ANDROID_TAG, "Error writing document", e) }
    }

}