package com.example.playlistmaker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.*

class TrackViewHolder(
    private val binding: TrackViewBinding,
    private val clicklListener: SearchTracksAdapter.TrackClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Track) {

        binding.trackName.text = item.trackName
        binding.artistName.text = item.artistName
        binding.trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTime)

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(5))
            .placeholder(R.drawable.placeholder_image)
            .into(binding.albumArt)

        itemView.setOnClickListener { clicklListener.onTrackClick(track = item) }
    }

}