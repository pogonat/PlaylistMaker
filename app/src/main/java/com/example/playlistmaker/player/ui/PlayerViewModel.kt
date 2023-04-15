package com.example.playlistmaker.player.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.ui.models.PlayerScreenState
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.PlayerStatus
import com.example.playlistmaker.search.domain.SearchResultStatus
import com.example.playlistmaker.search.domain.SearchTrackResult
import com.example.playlistmaker.search.domain.Track

class PlayerViewModel(application: Application): AndroidViewModel(application) {

    private val playerInteractor = Creator.providePlayerInteractor(getApplication<Application>())
    private val trackPlayer = Creator.provideTrackPlayer()

    private val stateLiveData = MutableLiveData<PlayerScreenState>()
    private val playStatusLiveData = MutableLiveData<PlayerStatus>()

    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = stateLiveData
    fun getPayerStateLiveData(): LiveData<PlayerStatus> = playStatusLiveData

    val foundTrack = mutableListOf<Track>()

    fun loadTrack(trackId: String) {
        foundTrack.clear()
        renderState(PlayerScreenState.Loading)
        getTrackById(trackId)
    }

    private fun renderState(state: PlayerScreenState) {
        stateLiveData.postValue(state)
    }

    private fun renderPlayer(playerStatus: PlayerStatus) {
        playStatusLiveData.postValue(playerStatus)
    }

    private fun getTrackById(trackId: String) {
        playerInteractor.getTrackById(
            trackId,
            object : PlayerInteractor.PlayerConsumer {
                override fun consume(searchTrackResult: SearchTrackResult) {
                    if (searchTrackResult.resultTrackList != null) {
                        foundTrack.add(searchTrackResult.resultTrackList[0])
                    }

                    when (searchTrackResult.searchResultStatus) {
                        SearchResultStatus.ERROR_CONNECTION -> {
                            searchTrackById(trackId)
                        }
                        SearchResultStatus.NOTHING_FOUND -> {
                            searchTrackById(trackId)
                        }
                        SearchResultStatus.SUCCESS -> {
                            renderState(PlayerScreenState.Content(foundTrack[0]))
                        }
                    }
                }
            }
        )
    }

    private fun searchTrackById(trackId: String) {
        playerInteractor.getTrackById(
            trackId,
            object : PlayerInteractor.PlayerConsumer {
                override fun consume(searchTrackResult: SearchTrackResult) {
                    if (searchTrackResult.resultTrackList != null) {
                        foundTrack.add(searchTrackResult.resultTrackList[0])
                    }

                    when (searchTrackResult.searchResultStatus) {
                        SearchResultStatus.ERROR_CONNECTION -> {
                            renderState(PlayerScreenState.Destroy)
                        }
                        SearchResultStatus.NOTHING_FOUND -> {
                            renderState(PlayerScreenState.Destroy)
                        }
                        SearchResultStatus.SUCCESS -> {
                            renderState(PlayerScreenState.Content(foundTrack[0]))
                        }
                    }
                }
            }
        )
    }

    private fun getCurrentPlayStatus(): PlayerStatus {

        return  playStatusLiveData.value ?: PlayerStatus.Ready(isPlaying = false)
    }

//    fun play() {
//        trackPlayer.startPlayer(
//            trackId = trackId,
//            statusObserver =  object : TrackPlayer.StatusObserver {
//                override fun onProgress(progress: Float) {
//                    playStatusLiveData.value = getCurrentPlayStatus().copy(progress = progress)
//                }
//
//                override fun onStop() {
//                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
//                }
//
//                override fun onPlay() {
//                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
//                }
//            }
//        )
//    }

//    private fun presentTrack(track: Track?) {
//        when (track) {
//            null -> view.finishIfTrackNull()
//            else -> {
//                view.fillViews(track)
//                val trackUrl = track.getAudioPreviewUrl()
//                preparePlayer(trackUrl, onPrepared, onCompletion)
//            }
//        }
//    }

    fun playBackControl() {
        when (trackPlayer.getPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                trackPlayer.pausePlayer()
                renderPlayer(PlayerStatus.Paused(progress = getCurrentPosition(), isPlaying = false))
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED, PlayerState.STATE_DEFAULT -> {
                trackPlayer.startPlayer()
                renderPlayer(PlayerStatus.Playing(progress = getCurrentPosition(), isPlaying = false))
            }
        }

    }

    fun preparePlayer(trackUrl: String) {
        trackPlayer.preparePlayer(trackUrl)
    }

    fun pausePlayer() {
        trackPlayer.pausePlayer()
    }

    fun releasePlayer() {
        trackPlayer.releasePlayer()
    }

    fun getPlayerState(): PlayerState {
        return trackPlayer.getPlayerState()
    }

    fun getCurrentPosition(): Long {
        return trackPlayer.getCurrentPosition()
    }

//    fun enablePlayButton() {
//        view.enablePlayButton()
//    }

//    private val onPrepared: () -> Unit = {
//        view.enablePlayButton()
//        view.updatePlaybackControlButton()
//    }
//
//    private val onCompletion: () -> Unit = {
//        view.stopProgressUpdate()
//        view.updatePlaybackControlButton()
//    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}
