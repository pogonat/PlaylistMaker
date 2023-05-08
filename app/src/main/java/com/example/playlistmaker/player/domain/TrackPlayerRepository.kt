package com.example.playlistmaker.player.domain

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.domain.models.Track

interface TrackPlayerRepository {
    fun searchTrackById(trackId: String): Resource<List<Track>>
    fun getTrackById(trackId: String): Track?
}