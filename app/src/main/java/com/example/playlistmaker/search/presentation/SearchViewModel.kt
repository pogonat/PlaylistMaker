package com.example.playlistmaker.search.presentation

import androidx.lifecycle.*
import com.example.playlistmaker.core.debounce
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.presentation.models.SearchScreenState
import kotlinx.coroutines.launch

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchScreenState>()

    private var userInputSearchText: String? = null

    private val foundTracks = mutableListOf<Track>()
    private val historyTracks = mutableListOf<Track>()
    init {
        setTracksHistory()
    }

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { searchRequestText ->
            searchTracks(searchRequestText)
        }

    fun getScreenStateLiveData(): LiveData<SearchScreenState> = stateLiveData

    private fun searchTracks(searchRequestText: String) {

        foundTracks.clear()

        if (searchRequestText.isNotEmpty()) {

            renderState(SearchScreenState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(searchRequestText)
                    .collect { searchResult ->
                        processResult(searchResult)
                    }
            }
        }
    }

    private fun processResult(searchResult: SearchTrackResult) {

        if (searchResult.resultTrackList != null) {
            foundTracks.addAll(searchResult.resultTrackList)
        }

        when (searchResult.searchResultStatus) {
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

    fun searchDebounce(changedSearchText: String) {
        if (userInputSearchText != changedSearchText) {
            this.userInputSearchText = changedSearchText
            trackSearchDebounce(changedSearchText)
        }
    }

    fun saveItem(track: Track) {
        searchInteractor.saveTrack(track)
        getTracksHistory()
    }
    private fun setTracksHistory() {
        viewModelScope.launch {
            searchInteractor.getTracksHistory().collect { history ->
                historyTracks.clear()
                historyTracks.addAll(history)
            }
        }
    }

    fun getTracksHistory() {
        viewModelScope.launch {
            searchInteractor.getTracksHistory().collect { history ->
                historyTracks.clear()
                historyTracks.addAll(history)
                renderState(
                    SearchScreenState.Success(
                        foundTracks = foundTracks,
                        historyTracks = historyTracks
                    )
                )
            }
        }
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

    }

}