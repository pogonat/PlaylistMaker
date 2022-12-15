package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.adapters.Track

class SearchHistory(val sharedPrefs: SharedPreferences) {
    val tracksHistory = ArrayList<Track>()

    fun saveItem(track: Track) {
        tracksHistory.add(track)
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
}