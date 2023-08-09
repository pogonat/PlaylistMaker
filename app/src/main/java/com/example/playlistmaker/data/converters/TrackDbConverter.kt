package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TracksInPlaylistsEntity
import com.example.playlistmaker.domain.models.Track

class TrackDbConverter {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            largeArtworkUrl = track.largeArtworkUrl,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackDurationMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            createdTimeStamp = System.currentTimeMillis()
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName ?: "",
            track.artistName ?: "",
            track.trackDurationMillis ?: "",
            track.artworkUrl100 ?: "",
            track.collectionName ?: "",
            track.releaseDate ?: "",
            track.primaryGenreName ?: "",
            track.country ?: "",
            track.previewUrl ?: "",
            track.largeArtworkUrl ?: "",
            track.releaseDate ?: ""
        )
    }

    fun mapToTracksInPlaylistsEntity(track: Track): TracksInPlaylistsEntity {
        return TracksInPlaylistsEntity(
            track.trackId,
            track.artworkUrl100,
            track.largeArtworkUrl,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackTimeMillis,
            track.previewUrl,
            System.currentTimeMillis()
        )
    }
}