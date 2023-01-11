package com.example.playlistmaker.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.*
import kotlin.collections.ArrayList

class TrackAdapter(private val adapterContext: Context, private val searchHistory: SearchHistory) : RecyclerView.Adapter<TracksViewHolder>() {

    var tracks = ArrayList<Track>()

    private val gson = App.instance.gson


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_view, parent, false)

        return TracksViewHolder(view)

    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            val track = tracks[position]
            searchHistory.saveItem(track)
            val playerIntent = Intent(adapterContext, PlayerActivity::class.java)
            val jsonTrack = gson.toJson(track)
            playerIntent.putExtra(PlayerActivity.KEY_BUNDLE, jsonTrack)
            adapterContext.startActivity(playerIntent)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}