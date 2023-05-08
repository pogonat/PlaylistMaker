package com.example.playlistmaker.search.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ListItemTrackBinding
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(
    private val binding: ListItemTrackBinding,
    private val clickListener: SearchTracksAdapter.TrackClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Track) {

        binding.trackName.text = item.trackName
        binding.artistName.text = item.artistName
        binding.trackDuration.text = item.trackTime

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(5))
            .placeholder(R.drawable.placeholder_image)
            .into(binding.albumArt)

        itemView.setOnClickListener { clickListener.onTrackClick(track = item) }
    }

}