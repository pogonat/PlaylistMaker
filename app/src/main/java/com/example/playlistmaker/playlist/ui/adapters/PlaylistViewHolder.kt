package com.example.playlistmaker.playlist.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ListItemPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist

class PlaylistViewHolder(
    private val binding: ListItemPlaylistBinding,
    private val clickListener: PlaylistAdapter.PlaylistClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {

        binding.playlistTitle.text = playlist.playlistName

        binding.tracksQuantity.text = playlist.tracksQuantityText


        if (playlist.imagePath.isNotEmpty() == true) {
            Glide.with(binding.playlistCover)
                .load(playlist.imagePath)
                .centerCrop()
                .transform(RoundedCorners(5))
                .placeholder(R.drawable.placeholder_image)
                .into(binding.playlistCover)
        } else {
            binding.playlistCover.setImageResource(R.drawable.placeholder_image)
        }

        itemView.setOnClickListener { clickListener.onPlaylistClick(playlist = playlist) }

    }

}
