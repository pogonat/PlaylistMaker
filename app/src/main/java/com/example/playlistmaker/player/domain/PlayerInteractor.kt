package com.example.playlistmaker.player.domain

import com.example.playlistmaker.domain.models.SearchTrackResult

interface PlayerInteractor {

    fun searchTrackById(trackId: String, consumer: PlayerConsumer)
    fun getTrackById(trackId: String, consumer: PlayerConsumer)

    interface PlayerConsumer {
        fun consume(searchTrackResult: SearchTrackResult)
    }

}