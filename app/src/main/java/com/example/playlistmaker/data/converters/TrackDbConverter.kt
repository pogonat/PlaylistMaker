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
            createdTimeStamp = System.currentTimeMillis(),
            artworkUrl60 = track.artworkUrl60
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName ?: "",
            artistName = track.artistName ?: "",
            trackTimeMillis = track.trackDurationMillis ?: "",
            artworkUrl100 = track.artworkUrl100 ?: "",
            collectionName = track.collectionName ?: "",
            releaseDate = track.releaseDate ?: "",
            primaryGenreName = track.primaryGenreName ?: "",
            country = track.country ?: "",
            previewUrl = track.previewUrl ?: "",
            largeArtworkUrl = track.largeArtworkUrl ?: "",
            artworkUrl60 = track.artworkUrl60 ?: ""
        )
    }

    fun mapToTracksInPlaylistsEntity(track: Track): TracksInPlaylistsEntity {
        return TracksInPlaylistsEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            largeArtworkUrl = track.largeArtworkUrl,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTime = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            createdTimeStamp = System.currentTimeMillis(),
            artworkUrl60 = track.artworkUrl60
        )
    }

    fun mapFromTracksInPlaylistsEntity(tracksInPlaylistsEntity: TracksInPlaylistsEntity): Track {
        return Track(
            trackId = tracksInPlaylistsEntity.trackId,
            trackName = tracksInPlaylistsEntity.trackName ?: "",
            artistName = tracksInPlaylistsEntity.artistName ?: "",
            trackTimeMillis = tracksInPlaylistsEntity.trackTime ?: "",
            artworkUrl100 = tracksInPlaylistsEntity.artworkUrl100 ?: "",
            collectionName = tracksInPlaylistsEntity.collectionName ?: "",
            releaseDate = tracksInPlaylistsEntity.releaseDate ?: "",
            primaryGenreName = tracksInPlaylistsEntity.primaryGenreName ?: "",
            country = tracksInPlaylistsEntity.country ?: "",
            previewUrl = tracksInPlaylistsEntity.previewUrl ?: "",
            largeArtworkUrl = tracksInPlaylistsEntity.largeArtworkUrl ?: "",
            artworkUrl60 = tracksInPlaylistsEntity.artworkUrl60 ?: ""
        )
    }

}