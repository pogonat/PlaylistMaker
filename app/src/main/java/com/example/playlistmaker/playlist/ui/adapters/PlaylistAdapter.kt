package com.example.playlistmaker.playlist.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ListItemPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist

class PlaylistAdapter(
    private val clickListener: PlaylistClickListener): RecyclerView.Adapter<PlaylistViewHolder>() {

    private val playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {

        val view = LayoutInflater.from(parent.context)

        return PlaylistViewHolder(
            ListItemPlaylistBinding.inflate(view, parent, false),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updateAdapter(playlistsList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(playlistsList)
        notifyDataSetChanged()
    }

    interface PlaylistClickListener {

        fun onPlaylistClick(playlist: Playlist)

    }

}