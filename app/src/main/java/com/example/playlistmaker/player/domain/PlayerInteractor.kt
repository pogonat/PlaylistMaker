package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.search.domain.SearchTrackResult

interface PlayerInteractor {
    fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerState(): PlayerState
    fun getCurrentPosition(): Int
    fun searchTrackById(trackId: String, consumer: PlayerConsumer)
    fun getTrackById(trackId: String, consumer: PlayerConsumer)

    interface PlayerConsumer {
        fun consume(searchTrackResult: SearchTrackResult)
    }

}