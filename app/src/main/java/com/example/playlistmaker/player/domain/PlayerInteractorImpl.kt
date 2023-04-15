package com.example.playlistmaker.player.domain

import com.example.playlistmaker.Resource
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import java.util.concurrent.Executors

class PlayerInteractorImpl(private val trackRepository: TrackPlayerRepository) : PlayerInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrackById(trackId: String, consumer: PlayerInteractor.PlayerConsumer) {

        executor.execute {
            when (val resource = trackRepository.searchTrackById(trackId)) {
                is Resource.Success -> {
                    if (resource.data!!.isEmpty()) {
                        consumer.consume(
                            SearchTrackResult(
                                SearchResultStatus.NOTHING_FOUND,
                                resource.data
                            )
                        )
                    } else consumer.consume(
                        SearchTrackResult(
                            SearchResultStatus.SUCCESS,
                            resource.data
                        )
                    )
                }
                is Resource.Error -> {
                    consumer.consume(
                        SearchTrackResult(
                            SearchResultStatus.ERROR_CONNECTION,
                            null
                        )
                    )
                }
            }
        }

    }

    override fun getTrackById(trackId: String, consumer: PlayerInteractor.PlayerConsumer) {
        val track = mutableListOf<Track>()
        val trackFromStorage = trackRepository.getTrackById(trackId)
        if (trackFromStorage !== null) {
            track.add(trackFromStorage)
            consumer.consume(
                SearchTrackResult(
                    SearchResultStatus.SUCCESS,
                    track
                )
            )
        } else {
            consumer.consume(
                SearchTrackResult(
                    SearchResultStatus.NOTHING_FOUND,
                    null
                )
            )
        }
    }
}