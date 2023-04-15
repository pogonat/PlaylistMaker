package com.example.playlistmaker.data

import com.example.playlistmaker.App
import com.example.playlistmaker.search.domain.StorageKeys
import com.example.playlistmaker.search.domain.Track

class TrackStorageImpl: TrackStorage {

    private val sharedPrefs = App.instance.sharedPrefs
    private val gson = App.instance.gson
    private val storageHistoryKey = StorageKeys.SEARCH_HISTORY_KEY.toString()

    override fun getTrackById(trackId: String): Track? {
        val tracksHistory = getTracksHistory()
        for (track in tracksHistory) {
            if (track.trackId == trackId) {
                return track
            }
        }
        return null
    }

    override fun getTracksHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(storageHistoryKey, "")
        val tracksHistory = ArrayList<Track>()
        if (json !== "") tracksHistory.addAll(gson.fromJson(json, Array<Track>::class.java))
        return tracksHistory
    }

    override fun saveTrack(newTrack: Track): ArrayList<Track> {
        var tracksHistory: ArrayList<Track> = getTracksHistory()
        for (track in tracksHistory) {
            if (track.trackId == newTrack.trackId) {
                tracksHistory.remove(track)
                break
            }
        }
        tracksHistory.add(0, newTrack)
        if (tracksHistory.size > SEARCH_HISTORY_SIZE) {
            tracksHistory.removeLast()
        }
        sharedPrefs.edit()
            .putString(storageHistoryKey, gson.toJson(tracksHistory))
            .apply()
        return tracksHistory
    }

    override fun deleteItems() {
        sharedPrefs.edit().remove(storageHistoryKey).apply()
    }

    companion object {
        const val SEARCH_HISTORY_SIZE = 10
    }


}