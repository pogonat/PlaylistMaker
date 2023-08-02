package com.example.playlistmaker.media.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ListItemPlaylistPlayerBinding
import com.example.playlistmaker.domain.models.Playlist

class PlaylistPlayerViewHolder (
    private val binding: ListItemPlaylistPlayerBinding,
    private val clickListener: PlaylistPlayerAdapter.PlaylistClickListener
): RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {

        binding.playlistTitle.text = playlist.playlistName

        binding.tracksQuantity.text = playlist.tracksQuantityText

        Glide.with(binding.playlistCover)
            .load(playlist.imagePath)
            .centerCrop()
            .transform(RoundedCorners(4))
            .placeholder(R.drawable.placeholder_image)
            .into(binding.playlistCover)

        itemView.setOnClickListener { clickListener.onPlaylistClick(playlist = playlist) }
    }

}