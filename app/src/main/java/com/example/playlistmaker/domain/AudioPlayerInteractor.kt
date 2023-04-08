package com.example.playlistmaker.domain

interface AudioPlayerInteractor {
    fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerState(): Int
    fun getCurrentPosition(): Int
}