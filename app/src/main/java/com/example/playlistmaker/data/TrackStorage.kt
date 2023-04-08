package com.example.playlistmaker.data

import com.example.playlistmaker.App
import com.example.playlistmaker.domain.models.StorageKeys
import com.example.playlistmaker.domain.models.Track

class TrackStorage {
    private val sharedPrefs = App.instance.sharedPrefs
    private val gson = App.instance.gson
    private val storageHistoryKey = StorageKeys.SEARCH_HISTORY_KEY.toString()

    fun getTrackById(trackId: String): Track? {
        val json = sharedPrefs.getString(storageHistoryKey, "")
        val tracksHistory = ArrayList<Track>()
        if (json !== "") tracksHistory.addAll(gson.fromJson(json, Array<Track>::class.java))
        for (track in tracksHistory) {
            if (track.trackId == trackId) {
                return track
            }
        }
        return null
    }
}