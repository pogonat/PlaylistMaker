package com.example.playlistmaker.data

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.data.models.TracksResponse
import com.example.playlistmaker.data.models.TracksSearchRequest
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.player.domain.TrackPlayerRepository
import com.example.playlistmaker.search.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrackRepositoryImpl(
    private val networkSearch: NetworkSearch,
    private val trackStorage: TrackStorage
) : TrackRepository, TrackPlayerRepository {

    override fun searchTracks(searchInput: String): Flow<Resource<List<Track>>> = flow {
        val response = networkSearch.searchTracks(TracksSearchRequest(searchInput))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(SearchResultStatus.ERROR_CONNECTION))
            }

            200 -> {
                emit(Resource.Success(resultsToTrack(response as TracksResponse)))
            }

            else -> {
                emit(Resource.Error(SearchResultStatus.ERROR_CONNECTION))
            }
        }
    }

    override fun searchTrackById(trackId: String): Flow<Resource<List<Track>>> = flow {
        val response = networkSearch.searchTrackById(TracksSearchRequest(trackId))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(SearchResultStatus.ERROR_CONNECTION))
            }

            200 -> {
                emit(Resource.Success(resultsToTrack(response as TracksResponse)))
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

    private fun resultsToTrack(response: TracksResponse): List<Track> {
        return response.searchResults.map {
            Track(
                trackId = it.trackId,
                trackName = it.trackName ?: "",
                artistName = it.artistName ?: "",
                trackTime = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(it.trackTime?.toLong() ?: 0f),
                artworkUrl100 = it.artworkUrl100 ?: "",
                collectionName = it.collectionName ?: "",
                releaseDate = it.releaseDate ?: "",
                primaryGenreName = it.primaryGenreName ?: "",
                country = it.country ?: "",
                previewUrl = it.previewUrl ?: "",
                largeArtworkUrl = it.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
                    ?: "",
                collectionYear = it.releaseDate?.substring(0, 4) ?: ""
            )
        }
    }

}



