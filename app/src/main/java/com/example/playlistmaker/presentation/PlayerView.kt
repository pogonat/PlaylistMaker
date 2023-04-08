package com.example.playlistmaker.presentation

import com.example.playlistmaker.domain.models.Track

interface PlayerView {
    fun getTrackId(): String
    fun initViews()
    fun fillViews(track: Track)
    fun enablePlayButton()
    fun updatePlaybackControlButton()
    fun stopProgressUpdate()
    fun progressTextRenew()
    fun finishIfTrackNull()
}