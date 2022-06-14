
package com.licenta2022.musicplayerApp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.licenta2022.musicplayerApp.other.Constants.NEW_SONG


class SongAdapter(
    firestoreAdapterOptions: FirestoreRecyclerOptions<Song>
) : FirestoreRecyclerAdapter<Song, SongAdapter.SongViewHolder>(firestoreAdapterOptions) {


    lateinit var glide: RequestManager

    private lateinit var songTitleTextView : TextView
    private lateinit var songSubTitleTextView : TextView
    private lateinit var picture: ImageView



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int, song: Song) {

    glide = Glide.with(holder.itemView).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

        holder.itemView.findViewById<ImageButton>(R.id.add_to_playlist).apply{
            setOnClickListener {


                let{
                    context.startActivity(launchNextScreen(context,song))
                }

            }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(song)
            }
        }

        holder.bind(song)
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song) {

            songTitleTextView = itemView.findViewById(R.id.tvPrimary)
            songSubTitleTextView = itemView.findViewById(R.id.tvSecondary)
            picture = itemView.findViewById(R.id.ivItemImage)

           // songNameTextView.text = playlist.playlistName
            songTitleTextView.text = song.title
            songSubTitleTextView.text = song.subtitle
            glide.load(song.imageUrl).into(picture)

        }
    }


    protected var onItemClickListener: ((Song) -> Unit)? = null

    fun setItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

    fun launchNextScreen(context: Context, song: Song): Intent {
        val intent = Intent(context, Playlist_activity::class.java)
        intent.putExtra(NEW_SONG, song)
        return intent
    }


}

