package com.licenta2022.musicplayerApp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.licenta2022.musicplayerApp.Playlist_activity
import com.licenta2022.musicplayerApp.R
import com.licenta2022.musicplayerApp.data.entities.Song
import com.licenta2022.musicplayerApp.other.Constants
import kotlinx.android.synthetic.main.swipe_item.view.*


class SwipeSongAdapter(
    firestoreAdapterOptions: FirestoreRecyclerOptions<Song>
) : FirestoreRecyclerAdapter<Song, SwipeSongAdapter.SongViewHolder>(firestoreAdapterOptions) {


    lateinit var glide: RequestManager

    private lateinit var picture: ImageView



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.swipe_item, parent, false)

        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int, song: Song) {

        holder.itemView.apply{
            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }

        glide = Glide.with(holder.itemView).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
        )


        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(song)
            }
        }

        holder.bind(song)
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song) {

            itemView.tvPrimary.text = song.title


        }
    }


    protected var onItemClickListener: ((Song) -> Unit)? = null

    fun setItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

    fun launchNextScreen(context: Context, song: Song): Intent {
        val intent = Intent(context, Playlist_activity::class.java)
        intent.putExtra(Constants.NEW_SONG, song)
        return intent
    }


}

