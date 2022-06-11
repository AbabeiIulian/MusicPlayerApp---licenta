package com.licenta2022.musicplayerApp.adapters

import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.licenta2022.musicplayerApp.Playlist_activity
import com.licenta2022.musicplayerApp.R
import kotlinx.android.synthetic.main.activity_account.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
): BaseSongAdapter(R.layout.list_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply{
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            glide.load(song.imageUrl).into(ivItemImage)

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }

        }

        holder.itemView.findViewById<ImageButton>(R.id.add_to_playlist).apply{
            setOnClickListener {

                    Toast.makeText(context, "Am apasat butonul de pe pozitia ${position}", Toast.LENGTH_LONG).show()

                    let{
                        val intent = Intent(context, Playlist_activity::class.java)
                        context.startActivity(intent)
                    }
            }
        }


    }

}