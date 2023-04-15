package com.example.playlistmaker.player.domain

import com.example.playlistmaker.Resource
import com.example.playlistmaker.search.domain.Track

interface TrackPlayerRepository {
    fun searchTrackById(trackId: String): Resource<List<Track>>
    fun getTrackById(trackId: String): Track?
}