package com.example.playlistmaker.player.presentation

import androidx.lifecycle.*
import com.example.playlistmaker.core.debounce
import com.example.playlistmaker.domain.FavouritesInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.player.presentation.models.PlayerState
import com.example.playlistmaker.player.presentation.models.PlayerStatus
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.presentation.models.TrackToTrackUIModelConverter
import com.example.playlistmaker.presentation.models.TrackUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val trackPlayer: TrackPlayer,
    private val favouritesInteractor: FavouritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val trackToTrackUIModelConverter: TrackToTrackUIModelConverter
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<PlayerScreenState>()
    private val playStatusLiveData = MutableLiveData<PlayerStatus>()

    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    fun getPlayerStateLiveData(): LiveData<PlayerStatus> = playStatusLiveData

    private var timerJob: Job? = null

    private var foundTrack: TrackUIModel? = null

    private val favouriteDebounce =
        debounce<Track>(FAV_DEBOUNCE_DELAY_MILLIS, viewModelScope, false) { track ->
            toggleFavourite(track)
        }

    private val getPlaylistDebounce =
        debounce(PLAYLIST_DEBOUNCE_DELAY_MILLIS, viewModelScope, true) {
            getPlaylists()
        }

    fun loadTrack(trackId: String) {
        foundTrack = null
        renderState(PlayerScreenState.Loading)
        getTrackById(trackId)
    }

    fun clickPlaylistDebounce() {
        getPlaylistDebounce()
    }

    fun addTrackToPlaylist(playlist: Playlist, trackUIModel: TrackUIModel) {
        val trackIsInPlaylist = checkPlaylist(playlist, trackUIModel.trackId)
        if (trackIsInPlaylist) {
            screenStateLiveData.postValue(PlayerScreenState.ShowMessage(playlist.playlistName))
        } else {
            val track = trackToTrackUIModelConverter.map(trackUIModel)
            viewModelScope.launch(Dispatchers.IO) {
                playlistInteractor
                    .updatePlaylist(playlist, track)
                    .collect { result ->
                        when (result) {
                            true -> {
                                screenStateLiveData
                                    .postValue(PlayerScreenState.BottomSheetHidden(playlist.playlistName))
                            }

                            false -> {
                                screenStateLiveData
                                    .postValue(PlayerScreenState.ShowMessage(playlistTitle = null))
                            }
                        }
                    }
            }

        }
    }

    private fun checkPlaylist(playlist: Playlist, trackId: String): Boolean {
        return playlist.trackList?.toSet()?.contains(trackId) ?: false
    }

    private fun renderState(state: PlayerScreenState) {
        screenStateLiveData.postValue(state)
    }

    private fun renderPlayer(playerStatus: PlayerStatus) {
        playStatusLiveData.postValue(playerStatus)
    }

    private fun getTrackById(trackId: String) {
        if (trackId.isNotEmpty()) {
            viewModelScope.launch {
                playerInteractor
                    .getTrackById(trackId)
                    .collect { result ->
                        if (result == null) {
                            searchTrackById(trackId)
                        } else {
                            val trackUIModel = trackToTrackUIModelConverter.map(result)
                            foundTrack = trackUIModel
                            renderState(PlayerScreenState.Content(trackUIModel))
                            trackPlayer.preparePlayer(trackUIModel.previewUrl)
                        }
                    }
            }
        }
    }

    private fun searchTrackById(trackId: String) {
        viewModelScope.launch {
            playerInteractor
                .searchTrackById(trackId)
                .collect { searchResult ->
                    processResult(searchResult)
                }
        }
    }

    private fun processResult(searchResult: SearchTrackResult) {

        when (searchResult.searchResultStatus) {
            SearchResultStatus.ERROR_CONNECTION -> {
                renderState(PlayerScreenState.Destroy)
            }

            SearchResultStatus.NOTHING_FOUND -> {
                renderState(PlayerScreenState.Destroy)
            }

            SearchResultStatus.SUCCESS -> {
                if (searchResult.resultTrackList != null) {
                    val trackFromResult = searchResult.resultTrackList[0]
                    val trackUIModel = trackToTrackUIModelConverter.map(trackFromResult)
                    foundTrack = trackUIModel
                    renderState(PlayerScreenState.Content(trackUIModel))
                    trackPlayer.preparePlayer(trackUIModel.previewUrl)
                }
            }
        }
    }

    fun playBackControl() {
        when (getPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_DEFAULT -> {
                trackPlayer.preparePlayer(foundTrack!!.previewUrl)
                startPlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_COMPLETE -> {
                renderPlayer(PlayerStatus.Complete)
            }
        }

    }

    fun toggleFavouriteDebounce(trackUIModel: TrackUIModel) {
        val track = trackToTrackUIModelConverter.map(trackUIModel)
        favouriteDebounce(track)
    }

    private fun toggleFavourite(track: Track) {
        when (track.isFavourite) {
            true -> viewModelScope.launch {
                favouritesInteractor.deleteFavourite(track)
                track.isFavourite = false
                val trackUIModel = trackToTrackUIModelConverter.map(track)
                renderState(PlayerScreenState.Content(trackUIModel))
            }

            false -> viewModelScope.launch {
                favouritesInteractor.saveFavourite(track)
                track.isFavourite = true
                val trackUIModel = trackToTrackUIModelConverter.map(track)
                renderState(PlayerScreenState.Content(trackUIModel))
            }
        }
    }



    private fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { foundPlaylist ->
                    val playlists = mutableListOf<Playlist>()
                    playlists.addAll(foundPlaylist)
                    screenStateLiveData.postValue(PlayerScreenState.BottomSheet(playlists.toList()))
                }
        }
    }

    private fun startPlayer() {
        trackPlayer.startPlayer()
        renderPlayer(PlayerStatus.Playing(progress = trackPlayer.getCurrentPosition()))
        startTimer()
    }

    fun pausePlayer() {
        trackPlayer.pausePlayer()
        timerJob?.cancel()
        renderPlayer(PlayerStatus.Paused(progress = trackPlayer.getCurrentPosition()))
    }

    fun releasePlayer() {
        trackPlayer.releasePlayer()
    }

    private fun getPlayerState(): PlayerState {
        return trackPlayer.getPlayerState()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (getPlayerState() == PlayerState.STATE_PLAYING) {
                delay(TIMER_DELAY_MILLIS)
                renderCurrentPosition()
            }
        }
    }

    private fun renderCurrentPosition() {
        if (getPlayerState() == PlayerState.STATE_COMPLETE) {
            trackPlayer.resetPlayer()
            renderPlayer(PlayerStatus.Complete)
        } else {
            renderPlayer(PlayerStatus.Playing(progress = trackPlayer.getCurrentPosition()))
        }
    }

    companion object {
        private const val TIMER_DELAY_MILLIS = 300L
        private const val FAV_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val PLAYLIST_DEBOUNCE_DELAY_MILLIS = 2000L
    }

}
