package com.example.playlistmaker.data

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.data.models.NetworkResultCode
import com.example.playlistmaker.data.models.TracksResponse
import com.example.playlistmaker.data.models.TracksResponseToTrackMapper
import com.example.playlistmaker.data.models.TracksSearchRequest
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.player.domain.TrackPlayerRepository
import com.example.playlistmaker.search.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.collections.ArrayList

class TrackRepositoryImpl(
    private val networkSearch: NetworkSearch,
    private val trackStorage: TrackStorage,
    private val mapper: TracksResponseToTrackMapper
) : TrackRepository, TrackPlayerRepository {

    override fun searchTracks(searchInput: String): Flow<Resource<List<Track>>> = flow {
        val response = networkSearch.searchTracks(TracksSearchRequest(searchInput))

        when (response.resultCode) {
            NetworkResultCode.CONNECTION_ERROR -> {
                emit(Resource.Error(SearchResultStatus.ERROR_CONNECTION))
            }

            NetworkResultCode.SUCCESS -> {
                emit(Resource.Success(mapper(response as TracksResponse)))
            }

            else -> {
                emit(Resource.Error(SearchResultStatus.ERROR_CONNECTION))
            }
        }
    }

    override fun searchTrackById(trackId: String): Flow<Resource<List<Track>>> = flow {
        val response = networkSearch.searchTrackById(TracksSearchRequest(trackId))

        when (response.resultCode) {
            NetworkResultCode.CONNECTION_ERROR -> {
                emit(Resource.Error(SearchResultStatus.ERROR_CONNECTION))
            }

            NetworkResultCode.SUCCESS -> {
                emit(Resource.Success(mapper(response as TracksResponse)))
            }

            else -> {
                emit(Resource.Error(SearchResultStatus.ERROR_CONNECTION))
            }
        }
    }

    override fun getTrackById(trackId: String): Flow<Track?> = flow {
        emit(trackStorage.getTrackById(trackId))
    }

    override fun saveTrack(track: Track): ArrayList<Track> {
        return trackStorage.saveTrack(newTrack = track)
    }

    override fun getTracksHistory(): ArrayList<Track> {
        return trackStorage.getTracksHistory()
    }

    override fun clearTracksHistory() {
        trackStorage.deleteItems()
    }

}



