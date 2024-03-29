package com.example.playlistmaker.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.data.TrackStorage
import com.example.playlistmaker.domain.models.StorageKeys
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class TrackStorageImpl(
    private val gson: Gson,
    private val sharedPrefs: SharedPreferences
) : TrackStorage {

    private val storageHistoryKey = StorageKeys.SEARCH_HISTORY_KEY

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

    override fun saveTrack(newTrack: Track) {
        val tracksHistory: ArrayList<Track> = getTracksHistory()
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
    }

    override fun deleteItems() {
        sharedPrefs.edit().remove(storageHistoryKey).apply()
    }

    companion object {
        const val SEARCH_HISTORY_SIZE = 10
    }


}