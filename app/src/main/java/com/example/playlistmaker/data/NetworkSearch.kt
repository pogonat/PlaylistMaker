package com.example.playlistmaker.data

import com.example.playlistmaker.data.models.Response

interface NetworkSearch {

    fun searchTracks(dto: Any): Response
    fun searchTrackById(dto: Any): Response

}