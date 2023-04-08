package com.example.playlistmaker.presentation

import com.example.playlistmaker.domain.AudioPlayerInteractor
import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.domain.models.PlayerState
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.PlayerPresenter

class PlayerPresenterImpl(
    private val view: PlayerView,
    private val audioPlayer: AudioPlayerInteractor,
    private val repository: TrackRepository
) : PlayerPresenter {

    override fun loadTrack() {

        val trackId = view.getTrackId()

        repository.getTrackById(trackId = trackId, ::presentTrack)

        view.initViews()
    }

    private fun presentTrack(track: Track?) {
        when (track) {
            null -> view.finishIfTrackNull()
            else -> {
                view.fillViews(track)
                val trackUrl = track.getAudioPreviewUrl()
                preparePlayer(trackUrl, onPrepared, onCompletion)
            }
        }
    }

    override fun playBackControl() {
        when (audioPlayer.getPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                audioPlayer.pausePlayer()
                view.updatePlaybackControlButton()
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED, PlayerState.STATE_DEFAULT -> {
                audioPlayer.startPlayer()
                view.progressTextRenew()
                view.updatePlaybackControlButton()
            }
        }

    }

    override fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        audioPlayer.preparePlayer(trackUrl, onPrepared, onCompletion)
    }

    override fun pausePlayer() {
        audioPlayer.pausePlayer()
    }

    override fun releasePlayer() {
        audioPlayer.releasePlayer()
    }

    override fun getPlayerState(): PlayerState {
        return audioPlayer.getPlayerState()
    }

    override fun getCurrentPosition(): Int {
        return audioPlayer.getCurrentPosition()
    }

    override fun enablePlayButton() {
        view.enablePlayButton()
    }

    private val onPrepared: () -> Unit = {
        view.enablePlayButton()
        view.updatePlaybackControlButton()
    }

    private val onCompletion: () -> Unit = {
        view.stopProgressUpdate()
        view.updatePlaybackControlButton()
    }
}