package com.example.playlistmaker.data

import com.example.playlistmaker.domain.models.Track

interface TrackStorage {

    fun getTrackById(trackId: String): Track?

    fun getTracksHistory(): ArrayList<Track>

    fun saveTrack(newTrack: Track)

    fun deleteItems()
}