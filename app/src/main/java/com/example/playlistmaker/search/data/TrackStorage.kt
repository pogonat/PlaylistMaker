package com.example.playlistmaker.search.data

import com.example.playlistmaker.App
import com.example.playlistmaker.search.domain.StorageKeys
import com.example.playlistmaker.search.domain.Track

interface TrackStorage {

    fun getTrackById(trackId: String): Track?

    fun getTracksHistory(): ArrayList<Track>

    fun saveTrack(newTrack: Track)

    fun deleteItems()
}