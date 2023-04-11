package com.example.playlistmaker.ui

import com.example.playlistmaker.domain.models.PlayerState

interface PlayerPresenter {
    fun loadTrack()
    fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerState(): PlayerState
    fun getCurrentPosition(): Int
    fun playBackControl()
    fun enablePlayButton()
}