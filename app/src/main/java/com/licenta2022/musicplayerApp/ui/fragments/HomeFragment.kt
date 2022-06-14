
package com.licenta2022.musicplayerApp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.licenta2022.musicplayerApp.R
import com.licenta2022.musicplayerApp.adapters.SongAdapter
import com.licenta2022.musicplayerApp.data.entities.Song
import com.licenta2022.musicplayerApp.other.Constants.DB_Playlist
import com.licenta2022.musicplayerApp.other.Constants.SONG_COLLECTION
import com.licenta2022.musicplayerApp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    lateinit var mainViewModel: MainViewModel

    lateinit var songAdapter: SongAdapter

    private lateinit var query : Query

    private val firestore = FirebaseFirestore.getInstance()
    lateinit var rvAllSongs : RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setupRecyclerView(view)


        songAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)

        }


    }

    private fun setupRecyclerView(view: View) {
       val playlistIds = activity?.intent?.getStringExtra("playlistId")
        Log.d("AndroidTag", playlistIds.toString())

        if(playlistIds !== null) {
            Log.d("AndroidTag", "getSongsByPlaylist() custom path")

            query  = firestore.collection(DB_Playlist).document(playlistIds!!).collection(SONG_COLLECTION)
        } else {
            Log.d("AndroidTag", "getSongsByPlaylist() base path")
            query  = firestore
                .collection(SONG_COLLECTION)
        }


        val options: FirestoreRecyclerOptions<Song> = FirestoreRecyclerOptions.Builder<Song>()
            .setQuery(query, Song::class.java)
            .build()

        songAdapter = SongAdapter(options)


        rvAllSongs = view.findViewById(R.id.rvAllSongs)



        rvAllSongs.adapter = songAdapter
    }

    override fun onStart() {
        super.onStart()
        songAdapter.startListening()
        songAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        songAdapter.stopListening()
    }

    private fun setupRecyclerView() = rvAllSongs.apply {
        adapter = songAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_acc, menu)

        val search = menu.findItem(R.id.action_search)
        val searchView = search.actionView as SearchView

        searchView.queryHint = "Search"

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {


            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                /*perform filtering*/
                Log.d("myItems", "filtering by $newText")

                val originalListOptions = FirestoreRecyclerOptions.Builder<Song>()
                    .setQuery(query,Song::class.java)
                    .setLifecycleOwner(viewLifecycleOwner)
                    .build()




                val filteredListQuery = firestore.collection(SONG_COLLECTION)
                    .orderBy("title")
                    .startAt(newText)
                    .endAt(newText + "\uf8ff")



                val filteredListOptions = FirestoreRecyclerOptions.Builder<Song>()
                    .setQuery(filteredListQuery,Song::class.java)
                    .setLifecycleOwner(viewLifecycleOwner)
                    .build()



                if(newText == "")
                    songAdapter.updateOptions(originalListOptions)

                else {
                    songAdapter.updateOptions(filteredListOptions)

                }
                songAdapter.notifyDataSetChanged()
                return false
            }

        })

    }

}
