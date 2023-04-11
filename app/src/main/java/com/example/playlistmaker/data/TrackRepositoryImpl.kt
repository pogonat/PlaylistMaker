package com.example.playlistmaker.data

import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.domain.models.LookupTrackResults
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.Track

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

}



