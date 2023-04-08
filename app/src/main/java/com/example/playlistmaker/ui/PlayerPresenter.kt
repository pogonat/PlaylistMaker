package com.example.playlistmaker.ui

interface PlayerPresenter {
    fun loadTrack()
    fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerState(): Int
    fun getCurrentPosition(): Int
    fun playBackControl()
    fun enablePlayButton()
}