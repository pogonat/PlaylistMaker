package com.example.playlistmaker.data.models

import com.example.playlistmaker.core.DateTimeUtil
import com.example.playlistmaker.domain.models.Track

class TracksResponseToTrackMapper: (TracksResponse) -> List<Track> {

    override fun invoke(response: TracksResponse): List<Track> {
        return response.searchResults.map {
            Track(
                trackId = it.trackId,
                trackName = it.trackName ?: "",
                artistName = it.artistName ?: "",
                trackTime = DateTimeUtil.formatTrackProgressTime(it.trackTime),
                artworkUrl100 = it.artworkUrl100 ?: "",
                collectionName = it.collectionName ?: "",
                releaseDate = it.releaseDate ?: "",
                primaryGenreName = it.primaryGenreName ?: "",
                country = it.country ?: "",
                previewUrl = it.previewUrl ?: "",
                largeArtworkUrl = it.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
                    ?: "",
                collectionYear = DateTimeUtil.formatCollectionYear(it.releaseDate)
            )
        }
    }
}

