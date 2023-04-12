package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.LookupTrackResults
import com.example.playlistmaker.search.domain.SearchTrackResult
import com.example.playlistmaker.search.domain.Track

class TrackRepositoryImpl(
    private val networkSearch: NetworkSearch,
    private val trackStorage: TrackStorage
) : TrackRepository {

    override fun searchTracks(
        searchInput: String,
        callback: (searchResult: SearchTrackResult) -> Unit
    ) {
        networkSearch.searchTracks(searchInput = searchInput, callback)
    }

    override fun getTrackById(trackId: String, callback: (track: Track?) -> Unit) {
        val handleTrackResult: (LookupTrackResults) -> Unit = { trackLookupResult ->
            when (trackLookupResult.resultTrackInfo) {
                null -> callback(trackStorage.getTrackById(trackId))
                else -> callback(trackLookupResult.resultTrackInfo!!)
            }
        }

        networkSearch.searchTrackById(trackId, handleTrackResult)
    }

    override fun getTracksHistory(): ArrayList<Track> {
        return trackStorage.getTracksHistory()
    }

}



