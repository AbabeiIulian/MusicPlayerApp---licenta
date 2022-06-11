package com.licenta2022.musicplayerApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.licenta2022.musicplayerApp.adapters.PlaylistAdapter
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class Playlist_activity : AppCompatActivity(), PlaylistAdapter.OnPlaylistClickListener {
    private lateinit var playListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        setupRecyclerView()

    }



    private fun setupRecyclerView() {
        val playListNames = listOf(
            "Rock",
            "Pop",
            "Pop",
            "Pop",
            "Trap"
        )

        val playlistAdapter = PlaylistAdapter(playListNames, this)


        playListRecyclerView = findViewById(R.id.playlistRecyclerID)



        playListRecyclerView.adapter = playlistAdapter
    }

    override fun onPlaylistClickListener(playlistName: String) {
        Toast.makeText(applicationContext, playlistName, Toast.LENGTH_SHORT).show()
    }
}