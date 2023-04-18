package com.example.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.search.domain.*
import com.example.playlistmaker.search.ui.models.SearchScreenState

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchInteractor = Creator.provideSearchInteractor(getApplication<Application>())

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchScreenState>()

    private var userInputSearchText: String? = null

    val foundTracks = mutableListOf<Track>()
    val historyTracks = initTrackHistory()

    fun getScreenStateLiveData(): LiveData<SearchScreenState> = stateLiveData

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun searchTracks(searchRequestText: String) {
        foundTracks.clear()
        if (searchRequestText.isNotEmpty()) {
            renderState(SearchScreenState.Loading)
            searchInteractor.searchTracks(
                searchRequestText,
                object : SearchInteractor.TracksConsumer {

                    override fun consume(searchTrackResult: SearchTrackResult) {
                        if (searchTrackResult.resultTrackList != null) {
                            foundTracks.addAll(searchTrackResult.resultTrackList)
                        }

                        when (searchTrackResult.searchResultStatus) {
                            SearchResultStatus.ERROR_CONNECTION -> {
                                userInputSearchText = ""
                                renderState(SearchScreenState.ErrorConnection)
                            }
                            SearchResultStatus.NOTHING_FOUND -> {
                                renderState(SearchScreenState.NothingFound)
                            }
                            SearchResultStatus.SUCCESS -> {
                                renderState(
                                    SearchScreenState.Success(
                                        foundTracks = foundTracks,
                                        historyTracks = historyTracks
                                    )
                                )
                            }
                        }

                    }
                })
        }
    }

    fun searchDebounce(changedSearchText: String) {

        if (userInputSearchText == changedSearchText) {
            return
        }

        this.userInputSearchText = changedSearchText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTracks(changedSearchText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun saveItem(track: Track) {
        val history = searchInteractor.saveTrack(track)
        historyTracks.clear()
        historyTracks.addAll(history)
        renderState(
            SearchScreenState.Success(
                foundTracks = foundTracks,
                historyTracks = historyTracks
            )
        )
    }

    fun initTrackHistory(): MutableList<Track> {
        return searchInteractor.getTracksHistory()
    }

    fun getTracksHistory() {
        val history = searchInteractor.getTracksHistory()
        historyTracks.clear()
        historyTracks.addAll(history)
        renderState(
            SearchScreenState.Success(
                foundTracks = foundTracks,
                historyTracks = historyTracks
            )
        )
    }

    fun clearTracksHistory() {
        searchInteractor.clearTracksHistory()
        historyTracks.clear()
        renderState(
            SearchScreenState.Success(
                foundTracks = foundTracks,
                historyTracks = historyTracks
            )
        )
    }

    fun clearSearchResults() {
        foundTracks.clear()
        renderState(
            SearchScreenState.Success(
                foundTracks = foundTracks,
                historyTracks = historyTracks
            )
        )
    }

    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    companion object {

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

}