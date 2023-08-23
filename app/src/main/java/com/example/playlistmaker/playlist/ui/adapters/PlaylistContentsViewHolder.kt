package com.example.playlistmaker.playlist.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ListItemTrackBinding
import com.example.playlistmaker.presentation.models.TrackUIModel

class PlaylistContentsViewHolder(
    private val binding: ListItemTrackBinding,
    private val clickListener: PlaylistContentsAdapter.TrackClickListener,
    private val onLongClickListener: PlaylistContentsAdapter.TrackLongClickListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: TrackUIModel) {

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackDuration.text = track.trackDurationFormatted

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(5))
            .placeholder(R.drawable.placeholder_image)
            .into(binding.albumArt)

        itemView.setOnClickListener { clickListener.onTrackClick(track = track) }

        itemView.setOnLongClickListener {
            onLongClickListener.onLongTrackClick(track = track)
            true
        }
    }
}