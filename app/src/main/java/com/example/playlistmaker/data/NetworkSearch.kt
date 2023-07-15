package com.example.playlistmaker.data

import com.example.playlistmaker.data.models.Response
import com.example.playlistmaker.data.models.TracksSearchRequest

interface NetworkSearch {

    suspend fun searchTracks(dto: TracksSearchRequest): Response
    suspend fun searchTrackById(dto: TracksSearchRequest): Response

}