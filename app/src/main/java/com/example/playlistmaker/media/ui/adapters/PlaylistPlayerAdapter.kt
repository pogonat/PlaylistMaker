package com.example.playlistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ListItemPlaylistPlayerBinding
import com.example.playlistmaker.domain.models.Playlist

class PlaylistPlayerAdapter(private val playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistPlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistPlayerViewHolder {

        val view = LayoutInflater.from(parent.context)

        return PlaylistPlayerViewHolder(
            ListItemPlaylistPlayerBinding.inflate(view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaylistPlayerViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

}