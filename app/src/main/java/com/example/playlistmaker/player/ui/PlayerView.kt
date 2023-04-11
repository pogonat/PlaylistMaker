package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.Track

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