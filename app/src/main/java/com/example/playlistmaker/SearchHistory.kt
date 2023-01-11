package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.adapters.Track
import com.google.gson.Gson

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    var tracksHistory = ArrayList<Track>()
    private val gson = App.instance.gson

    fun loadTracksFromJson() {
        val json = sharedPrefs.getString(SearchActivity.SEARCH_HISTORY_KEY, "")
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
            .putString(SearchActivity.SEARCH_HISTORY_KEY, toJson())
            .apply()
    }

    fun deleteItems() {
        tracksHistory.clear()
        sharedPrefs.edit().remove(SearchActivity.SEARCH_HISTORY_KEY).apply()
    }

    private fun toJson(): String {
        return gson.toJson(tracksHistory)
    }

    companion object {
        const val SEARCH_HISTORY_SIZE = 10
    }

}