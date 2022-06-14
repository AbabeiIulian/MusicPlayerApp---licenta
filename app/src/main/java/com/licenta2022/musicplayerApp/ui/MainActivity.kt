package com.licenta2022.musicplayerApp.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.licenta2022.musicplayerApp.Loggin_activity
import com.licenta2022.musicplayerApp.R
import com.licenta2022.musicplayerApp.adapters.SongAdapter
import com.licenta2022.musicplayerApp.adapters.SwipeSongAdapter
import com.licenta2022.musicplayerApp.data.entities.Song
import com.licenta2022.musicplayerApp.exoplayer.isPlaying
import com.licenta2022.musicplayerApp.exoplayer.toSong
import com.licenta2022.musicplayerApp.other.Constants
import com.licenta2022.musicplayerApp.other.Status.*
import com.licenta2022.musicplayerApp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    lateinit var swipeSongAdapter: SwipeSongAdapter
    private lateinit var query : Query
    private val firestore = FirebaseFirestore.getInstance()
    private var songs: List<Song>? = null

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        subscribeToObservers()

        setupVpSong()


        ivPlayPause.setOnClickListener{
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        swipeSongAdapter.setItemClickListener {
            navHostFragment.findNavController().navigate(
                R.id.globalActionToSongFragment
            )
        }

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.songFragment -> hideBottomBar()
                R.id.homeFragment -> showBottomBar()
                else-> showBottomBar()
            }
        }

    }



    private fun setupVpSong() {
        val playlistIds = intent.getStringExtra("playlistId")

        if(playlistIds !== null) {
            query  = firestore.collection(Constants.DB_Playlist).document(playlistIds).collection(
                Constants.SONG_COLLECTION
            )
        } else {
            query  = firestore
                .collection(Constants.SONG_COLLECTION)
        }



    query.addSnapshotListener { value, error ->
            if (value !== null) {
                 songs = value.toObjects(Song::class.java)

                Log.d("AndroidTag2", songs.toString())
            }
        }






        val options: FirestoreRecyclerOptions<Song> = FirestoreRecyclerOptions.Builder<Song>()
            .setQuery(query, Song::class.java)
            .build()

        swipeSongAdapter = SwipeSongAdapter(options)




        vpSong.adapter = swipeSongAdapter

        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(playbackState?.isPlaying == true){
                    songs?.get(position)?.let { mainViewModel.playOrToggleSong(it) }
                }else{
                    curPlayingSong = songs?.get(position)
                }
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_fav -> {
            // do stuff
            val intent = Intent(this, Loggin_activity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun hideBottomBar() {
        ivCurSongImage.isVisible = false
        vpSong.isVisible = false
        ivPlayPause.isVisible = false
    }

    private fun showBottomBar() {
        ivCurSongImage.isVisible = true
        vpSong.isVisible = true
        ivPlayPause.isVisible = true
    }

    private fun switchViewPagerToCurrentSong(song: Song){
        val newItemIndex = songs?.indexOf(song)

        if (newItemIndex != -1){
            if (newItemIndex != null) {
                vpSong.currentItem = newItemIndex
            }
            curPlayingSong = song
        }

    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(this){
            it?.let { result ->
                when(result.status){
                    SUCCESS -> {
                        result.data?.let { songs->
                            if(songs.isNotEmpty()){
                                glide.load((curPlayingSong ?: songs[0]).imageUrl).into(ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    ERROR -> Unit
                    LOADING -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(this){
            if(it==null) return@observe

            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUrl).into(ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        }

        mainViewModel.playbackState.observe(this){
            playbackState = it
            ivPlayPause.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
        mainViewModel.isConnected.observe(this){
            it?.getContentIfNotHandled()?.let { result->
                when(result.status){
                    ERROR -> Snackbar.make(
                        rootLayout,
                        result.message ?: "An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
        mainViewModel.networkError.observe(this){
            it?.getContentIfNotHandled()?.let { result->
                when(result.status){
                    ERROR -> Snackbar.make(
                        rootLayout,
                        result.message ?: "An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        swipeSongAdapter.startListening()
        swipeSongAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        swipeSongAdapter.stopListening()
    }



}