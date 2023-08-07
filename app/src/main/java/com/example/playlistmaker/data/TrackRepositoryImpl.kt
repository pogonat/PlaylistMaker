package com.example.playlistmaker.data

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.data.models.NetworkResultCode
import com.example.playlistmaker.data.models.TracksResponse
import com.example.playlistmaker.data.converters.TracksResponseToTrackMapper
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.models.TracksSearchRequest
import com.example.playlistmaker.data.network.NetworkSearch
import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.player.domain.TrackPlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.collections.ArrayList

class TrackRepositoryImpl(
    private val networkSearch: NetworkSearch,
    private val trackStorage: TrackStorage,
    private val appDatabase: AppDatabase,
    private val mapper: TracksResponseToTrackMapper
) : TrackRepository, TrackPlayerRepository {

    override fun searchTracks(searchInput: String): Flow<Resource<List<Track>>> = flow {
        val response = networkSearch.searchTracks(TracksSearchRequest(searchInput))

        when (response.resultCode) {
            NetworkResultCode.CONNECTION_ERROR -> {
                emit(Resource.Error(SearchResultStatus.ERROR_CONNECTION))
            }

            NetworkResultCode.SUCCESS -> {
                val foundTracks = mapper(response as TracksResponse)
                emit(Resource.Success(markResultFavourites(foundTracks)))
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
                val foundTracks = mapper(response as TracksResponse)
                emit(Resource.Success(markResultFavourites(foundTracks)))
            }

            else -> {
                emit(Resource.Error(SearchResultStatus.ERROR_CONNECTION))
            }
        }
    }

    override fun getTrackById(trackId: String): Flow<Track?> = flow {
        val track = trackStorage.getTrackById(trackId)
        if (track != null) {
            val favTracks = appDatabase.trackDao().getIdsFromFavourites().toSet()
            track.isFavourite = favTracks.contains(track.trackId)
        }
        emit(track)
    }

    override fun saveTrack(track: Track) {
        trackStorage.saveTrack(newTrack = track)
    }

    override fun getTracksHistory(): Flow<ArrayList<Track>> = flow {
        emit(trackStorage.getTracksHistory())
    }

    override fun clearTracksHistory() {
        trackStorage.deleteItems()
    }

    private suspend fun markResultFavourites(tracks: List<Track>): List<Track> {
        val favTracks = appDatabase.trackDao().getIdsFromFavourites().toSet()
        val resultMarkedFavourites = tracks.map { track ->
            track.copy(isFavourite = favTracks.contains(track.trackId))
        }
        return resultMarkedFavourites
    }

}



