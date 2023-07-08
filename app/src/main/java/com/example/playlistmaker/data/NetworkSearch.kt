package com.example.playlistmaker.data

import com.example.playlistmaker.data.models.Response

interface NetworkSearch {

    suspend fun searchTracks(dto: Any): Response
    suspend fun searchTrackById(dto: Any): Response

}