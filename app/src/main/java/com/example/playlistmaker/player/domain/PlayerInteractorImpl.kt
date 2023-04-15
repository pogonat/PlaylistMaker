package com.example.playlistmaker.player.domain

import android.media.MediaPlayer
import com.example.playlistmaker.Resource
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.search.domain.*
import java.util.concurrent.Executors

class PlayerInteractorImpl(private val trackRepository: TrackPlayerRepository) : PlayerInteractor {

    private var playerState = PlayerState.STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()

    private val executor = Executors.newCachedThreadPool()


    override fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.STATE_PREPARED
            onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.STATE_PREPARED
            onCompletion()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

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