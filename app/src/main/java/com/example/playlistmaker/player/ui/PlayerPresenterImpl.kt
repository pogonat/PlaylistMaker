package com.example.playlistmaker.player.ui

import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.PlayerState
import com.example.playlistmaker.search.domain.Track

class PlayerPresenterImpl(
    private val view: PlayerView,
    private val audioPlayer: AudioPlayerInteractor,
    private val repository: TrackRepository
) : PlayerPresenter {

    override fun loadTrack() {

        val trackId = view.getTrackId()

        repository.searchTrackById(trackId = trackId)

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