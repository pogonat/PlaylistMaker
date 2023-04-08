package com.example.playlistmaker.data

import com.example.playlistmaker.App
import com.example.playlistmaker.domain.models.Track

class TrackStorage {
    private val sharedPrefs = App.instance.sharedPrefs
    private val gson = App.instance.gson

    fun getTrackById(trackId: String): Track {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, "")
        val tracksHistory = ArrayList<Track>()
        if (json !== "") tracksHistory.addAll(gson.fromJson(json, Array<Track>::class.java))
        val tracks = ArrayList<Track>()
        for (track in tracksHistory) {
            if (track.trackId == trackId) {
                tracks.add(track)
                return tracks[0]
            }
        }
        return tracks[0]
    }


    companion object {
        const val SEARCH_HISTORY_KEY = "key_for_search_history"
    }

}