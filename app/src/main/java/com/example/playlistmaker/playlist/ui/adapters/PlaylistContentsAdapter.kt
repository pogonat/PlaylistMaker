package com.example.playlistmaker.playlist.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ListItemTrackBinding
import com.example.playlistmaker.presentation.models.TrackUIModel

class PlaylistContentsAdapter(
    private val tracks: List<TrackUIModel>,
    private val clickListener: TrackClickListener,
    private val onLongClickListener: TrackLongClickListener
): RecyclerView.Adapter<PlaylistContentsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistContentsViewHolder {

        val view = LayoutInflater.from(parent.context)

        return PlaylistContentsViewHolder(
            ListItemTrackBinding.inflate(view, parent, false),
            clickListener,
            onLongClickListener
        )
    }

    override fun onBindViewHolder(holder: PlaylistContentsViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    interface TrackClickListener {
        fun onTrackClick(track: TrackUIModel)
    }

    interface TrackLongClickListener {
        fun onLongTrackClick(track: TrackUIModel)
    }

}