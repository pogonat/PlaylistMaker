package com.example.playlistmaker.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.source_track_name)
    private val artistName: TextView = itemView.findViewById(R.id.source_artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.source_track_duration)
    private val artworkUrl100: ImageView = itemView.findViewById((R.id.source_album_art))

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(artworkUrl100)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(5))
            .placeholder(R.drawable.placeholder_image)
            .into(artworkUrl100)
    }

}