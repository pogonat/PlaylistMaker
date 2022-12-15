package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.adapters.Track

class SearchHistory(val sharedPrefs: SharedPreferences) {
    val tracksHistory = ArrayList<Track>()

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
    }

    fun deleteItems() {
        tracksHistory.clear()
    }

//    fun getTracks() {
//        return this.tracksHistory
//    }
//
    interface Observer {
        fun saveItem(track: Track)
    }

    interface Observable {
        fun add(observer: Observer)
        fun remove(observer: Observer)
        fun notifyObservers(track: Track)
    }

    companion object {
        const val SEARCH_HISTORY_SIZE = 10
    }

}