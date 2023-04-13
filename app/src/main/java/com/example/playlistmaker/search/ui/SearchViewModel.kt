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
import com.example.playlistmaker.search.domain.*
import com.example.playlistmaker.search.ui.models.SearchScreenState

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchInteractor = Creator.provideSearchInteractor(getApplication<Application>())

    //    val trackRepository: TrackRepository = TrackRepositoryImpl(NetworkSearchImpl(), TrackStorage())
    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchScreenState>()
//    fun observeState(): LiveData<SearchScreenState> = stateLiveData

//    init {
//        searchInteractor.loadTrackData(
//            trackId
//        )
//    }

    //    change to "" as it was previously?
    private var userInputSearchText: String? = null

    fun getScreenStateLiveData(): LiveData<SearchScreenState> = stateLiveData

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun searchTracks(searchRequestText: String) {
        if (searchRequestText.isNotEmpty()) {
            renderState(SearchScreenState.Loading)
            searchInteractor.searchTracks(
                searchRequestText,
                object : SearchInteractor.TracksConsumer {

                    override fun consume(searchTrackResult: SearchTrackResult) {
                        val tracks = mutableListOf<Track>()
                        if (searchTrackResult.resultTrackList != null) {
                            tracks.addAll(searchTrackResult.resultTrackList)
                        }

                        when (searchTrackResult.searchResultStatus) {
                            SearchResultStatus.ERROR_CONNECTION -> {
                                renderState(SearchScreenState.ErrorConnection)
                            }
                            SearchResultStatus.NOTHING_FOUND -> {
                                renderState(SearchScreenState.NothingFound)
                            }
                            SearchResultStatus.SUCCESS -> {
                                renderState(SearchScreenState.Success(tracks = tracks))
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
        searchInteractor.saveTrack(track)
    }

    fun getTracksHistory(): ArrayList<Track> {
        return searchInteractor.getTracksHistory()
    }

    fun clearTracksHistory() {
        searchInteractor.clearTracksHistory()
    }

    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    companion object {
//        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

}