package com.example.playlistmaker.search.domain

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(private val trackRepository: TrackRepository) : SearchInteractor {

    override fun searchTracks(searchText: String): Flow<SearchTrackResult> {
        return trackRepository.searchTracks(searchText).map { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data?.isNotEmpty() == true) {
                        SearchTrackResult(SearchResultStatus.SUCCESS, result.data)
                    } else {
                        SearchTrackResult(SearchResultStatus.NOTHING_FOUND,result.data)
                    }
                }

                is Resource.Error -> {
                    SearchTrackResult(SearchResultStatus.ERROR_CONNECTION, null)
                }
            }
        }
    }

    override fun saveTrack(track: Track) {
        trackRepository.saveTrack(track)
    }

    override fun getTracksHistory(): Flow<List<Track>> {
        return trackRepository.getTracksHistory()
    }

    override fun clearTracksHistory() {
        trackRepository.clearTracksHistory()
    }

}