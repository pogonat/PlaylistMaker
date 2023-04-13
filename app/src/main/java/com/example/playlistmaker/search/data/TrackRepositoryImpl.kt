package com.example.playlistmaker.search.data

import com.example.playlistmaker.Resource
import com.example.playlistmaker.search.domain.*

class TrackRepositoryImpl(
    private val networkSearch: NetworkSearch,
    private val trackStorage: TrackStorage
) : TrackRepository {

    override fun searchTracks(searchInput: String): Resource<List<Track>> {
        val response = networkSearch.searchTracks(TracksSearchRequest(searchInput))

        return when (response.resultCode) {
            -1 -> {
                Resource.Error(SearchResultStatus.ERROR_CONNECTION)
            }

            200 -> {
                Resource.Success((response as TracksResponse).searchResults.map{
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName ?: "",
                        artistName = it.artistName ?: "",
                        trackTime = it.trackTime,
                        artworkUrl100 = it.artworkUrl100?: "",
                        collectionName = it.collectionName?: "",
                        releaseDate = it.releaseDate?: "",
                        primaryGenreName = it.primaryGenreName?: "",
                        country = it.country?: "",
                        previewUrl = it.previewUrl?: ""
                    )
                })
            }
            else -> {
                Resource.Error(SearchResultStatus.ERROR_CONNECTION)
            }
        }
    }

    override fun searchTrackById(trackId: String): Resource<List<Track>> {
        val response = networkSearch.searchTracks(TracksSearchRequest(trackId))

        return when (response.resultCode) {
            -1 -> {
                Resource.Error(SearchResultStatus.ERROR_CONNECTION)
            }

            200 -> {
                Resource.Success((response as TracksResponse).searchResults.map{
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName ?: "",
                        artistName = it.artistName ?: "",
                        trackTime = it.trackTime,
                        artworkUrl100 = it.artworkUrl100?: "",
                        collectionName = it.collectionName?: "",
                        releaseDate = it.releaseDate?: "",
                        primaryGenreName = it.primaryGenreName?: "",
                        country = it.country?: "",
                        previewUrl = it.previewUrl?: ""
                    )
                })
            }
            else -> {
                Resource.Error(SearchResultStatus.ERROR_CONNECTION)
            }
        }
    }

    override fun getTrackById(trackId: String): Track? {
        return (trackStorage.getTrackById(trackId))
    }

    override fun saveTrack(track: Track) {
        trackStorage.saveTrack(newTrack = track)
    }

    override fun getTracksHistory(): ArrayList<Track> {
        return trackStorage.getTracksHistory()
    }

    override fun clearTracksHistory() {
        trackStorage.deleteItems()
    }

}



