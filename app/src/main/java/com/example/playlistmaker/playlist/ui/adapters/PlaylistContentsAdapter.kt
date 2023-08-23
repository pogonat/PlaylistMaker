package com.example.playlistmaker.playlist.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ListItemTrackBinding
import com.example.playlistmaker.presentation.models.TrackUIModel

class PlaylistContentsAdapter(
    private val clickListener: TrackClickListener,
    private val onLongClickListener: TrackLongClickListener
): RecyclerView.Adapter<PlaylistContentsViewHolder>() {

    private val trackList = ArrayList<TrackUIModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistContentsViewHolder {

        val view = LayoutInflater.from(parent.context)

        return PlaylistContentsViewHolder(
            ListItemTrackBinding.inflate(view, parent, false),
            clickListener,
            onLongClickListener
        )
    }

    override fun onBindViewHolder(holder: PlaylistContentsViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun updateAdapter(newTrackList: List<TrackUIModel>) {
        trackList.clear()
        trackList.addAll(newTrackList)
        notifyDataSetChanged()
    }

    interface TrackClickListener {
        fun onTrackClick(track: TrackUIModel)
    }

    interface TrackLongClickListener {
        fun onLongTrackClick(track: TrackUIModel)
    }

}