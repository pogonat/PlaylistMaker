package com.example.playlistmaker.presentation

import android.content.SharedPreferences
import com.example.playlistmaker.App
import com.example.playlistmaker.domain.models.StorageKeys
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.SearchActivity

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    var tracksHistory = ArrayList<Track>()
    private val gson = App.instance.gson
    val storageKey = StorageKeys.SEARCH_HISTORY_KEY.toString()

    fun loadTracksFromJson() {
        val json = sharedPrefs.getString(storageKey, "")
        if (json !== "") tracksHistory.addAll(gson.fromJson(json, Array<Track>::class.java))
    }

    fun saveItem(newTrack: Track) {
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
            .putString(storageKey, toJson())
            .apply()
    }

    fun deleteItems() {
        tracksHistory.clear()
        sharedPrefs.edit().remove(storageKey).apply()
    }

    private fun toJson(): String {
        return gson.toJson(tracksHistory)
    }

    companion object {
        const val SEARCH_HISTORY_SIZE = 10
    }

}