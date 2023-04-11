package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.PlayerState

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