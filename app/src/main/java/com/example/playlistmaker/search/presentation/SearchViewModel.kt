package com.example.playlistmaker.search.presentation

import androidx.lifecycle.*
import com.example.playlistmaker.core.debounce
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.presentation.models.TrackToTrackUIModelConverter
import com.example.playlistmaker.presentation.models.TrackUIModel
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.presentation.models.SearchScreenState
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val trackToTrackUIModelConverter: TrackToTrackUIModelConverter
    ) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchScreenState>()

    private var userInputSearchText: String? = null

    private val foundTracks = mutableListOf<TrackUIModel>()
    private val historyTracks = mutableListOf<TrackUIModel>()
    init {
        setTracksHistory()
    }

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY_MILLIS, viewModelScope, true) { searchRequestText ->
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
            val foundTrackUIModels = trackToTrackUIModelConverter.mapListToTrackUIModels(searchResult.resultTrackList)
            foundTracks.addAll(foundTrackUIModels)
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
                        foundTracks = foundTracks.toList(),
                        historyTracks = historyTracks.toList()
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

    fun saveItem(trackUIModel: TrackUIModel) {
        val track = trackToTrackUIModelConverter.map(trackUIModel)
        searchInteractor.saveTrack(track)
        getTracksHistory()
    }
    private fun setTracksHistory() {
        viewModelScope.launch {
            searchInteractor.getTracksHistory().collect { history ->
                historyTracks.clear()
                val historyTrackUIModels = trackToTrackUIModelConverter.mapListToTrackUIModels(history)
                historyTracks.addAll(historyTrackUIModels)
            }
        }
    }

    fun getTracksHistory() {
        viewModelScope.launch {
            searchInteractor.getTracksHistory().collect { history ->
                historyTracks.clear()
                val historyTrackUIModels = trackToTrackUIModelConverter.mapListToTrackUIModels(history)
                historyTracks.addAll(historyTrackUIModels)
                renderState(
                    SearchScreenState.Success(
                        foundTracks = foundTracks.toList(),
                        historyTracks = historyTracks.toList()
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
                foundTracks = foundTracks.toList(),
                historyTracks = historyTracks.toList()
            )
        )
    }

    fun clearSearchResults() {
        foundTracks.clear()
        renderState(
            SearchScreenState.Success(
                foundTracks = foundTracks.toList(),
                historyTracks = historyTracks.toList()
            )
        )
    }

    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    companion object {

        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L

    }

}