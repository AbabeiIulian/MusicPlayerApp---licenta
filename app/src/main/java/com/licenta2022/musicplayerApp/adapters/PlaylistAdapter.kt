package com.licenta2022.musicplayerApp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.licenta2022.musicplayerApp.R

class PlaylistAdapter(
    private val dataSet: List<String>,
    private val onPlaylistClickListener: OnPlaylistClickListener
    ) : RecyclerView.Adapter<PlaylistAdapter.PlayListViewHolder>() {
    private lateinit var playlistNameTextView : TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item, parent, false)

        return PlayListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        val item = dataSet[position]


        holder.bind(item)
    }

    override fun getItemCount() = dataSet.size

    inner class PlayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(playListName: String) {
            playlistNameTextView = itemView.findViewById(R.id.playlistNameTextView)
            playlistNameTextView.text = playListName
            itemView.setOnClickListener {
                onPlaylistClickListener.onPlaylistClickListener(playListName)
            }
        }
    }

    interface OnPlaylistClickListener {
        fun onPlaylistClickListener(playlistName: String)
    }
}