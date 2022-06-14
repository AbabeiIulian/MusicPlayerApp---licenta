package com.licenta2022.musicplayerApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.licenta2022.musicplayerApp.R
import com.licenta2022.musicplayerApp.data.entities.Playlist

class PlaylistAdapter(
    private val onPlaylistClickListener: OnPlaylistClickListener,
    firestoreAdapterOptions: FirestoreRecyclerOptions<Playlist>
) : FirestoreRecyclerAdapter<Playlist ,PlaylistAdapter.PlayListViewHolder>(firestoreAdapterOptions) {


    private lateinit var playlistNameTextView : TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item, parent, false)

        return PlayListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int, playlist: Playlist) {



        holder.itemView.setOnClickListener {
            onPlaylistClickListener.onPlaylistClickListener(playlist, snapshots.getSnapshot(position).id)
        }

        holder.bind(playlist)
    }

    inner class PlayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(playlist: Playlist) {
            playlistNameTextView = itemView.findViewById(R.id.playlistNameTextView)
            playlistNameTextView.text = playlist.playlistName

        }
    }

    interface OnPlaylistClickListener {
        fun onPlaylistClickListener(playlist: Playlist, playListId: String)
    }
}