package com.example.playlistmaker.search.data

import com.example.playlistmaker.App
import com.example.playlistmaker.search.domain.StorageKeys
import com.example.playlistmaker.search.domain.Track

class TrackStorage {
    private val sharedPrefs = App.instance.sharedPrefs
    private val gson = App.instance.gson
    private val storageHistoryKey = StorageKeys.SEARCH_HISTORY_KEY.toString()

    fun getTrackById(trackId: String): Track? {
        val tracksHistory = getTracksHistory()
        for (track in tracksHistory) {
            if (track.trackId == trackId) {
                return track
            }
        }
        return null
    }

    fun getTracksHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(storageHistoryKey, "")
        val tracksHistory = ArrayList<Track>()
        if (json !== "") tracksHistory.addAll(gson.fromJson(json, Array<Track>::class.java))
        return tracksHistory
    }

}