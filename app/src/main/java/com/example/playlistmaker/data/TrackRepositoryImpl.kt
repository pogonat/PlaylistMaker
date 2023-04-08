package com.example.playlistmaker.data

import android.util.Log
import android.widget.Toast
import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.domain.models.LookupTrackResults
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.Track
import javax.security.auth.callback.Callback

class TrackRepositoryImpl(
    private val networkSearch: NetworkSearch,
    val trackStorage: TrackStorage
) : TrackRepository {


    override fun searchTracks(searchInput: String, callback: (searchResult: SearchTrackResult)-> Unit) {
        val result = SearchTrackResult(SearchResultStatus.NOTHING_FOUND, ArrayList<Track>())
        networkSearch.searchTracks(searchInput = searchInput, callback)
    }

    override fun getTrackById(trackId: String, callback: (track: Track) -> Unit) {
//        val trackLookupResult = networkSearch.searchTrackById(trackId, ::handleTrackResult, callback)
        val handleTrackResult: (LookupTrackResults) -> Unit = {trackLookupResult ->
//            when (trackLookupResult.resultTrackInfo) {
//                null -> trackStorage.getTrackById(trackId)
//                else -> trackLookupResult.resultTrackInfo!!
//            }

            Log.e("LAMBDA", trackLookupResult.toString())
            callback(trackLookupResult.resultTrackInfo!!)
        }

        networkSearch.searchTrackById(trackId, handleTrackResult)

//        when (trackLookupResult.resultTrackInfo) {
//            null -> return trackStorage.getTrackById(trackId)
//            else -> return trackLookupResult.resultTrackInfo!!
//        }
    }

//    fun handleTrackResult(trackLookupResult: LookupTrackResults, callback: (track: Track) -> Unit) {
//        when (trackLookupResult.resultTrackInfo) {
//            null -> return trackStorage.getTrackById(trackId)
//            else -> return trackLookupResult.resultTrackInfo!!
//        }
//    }
    
}



