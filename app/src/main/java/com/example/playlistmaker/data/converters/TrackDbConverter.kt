package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.models.Track

class TrackDbConverter {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.artworkUrl100,
            track.largeArtworkUrl,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackTime,
            track.previewUrl
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName?: "",
            track.artistName?: "",
            track.trackTime?: "",
            track.artworkUrl100?: "",
            track.collectionName?: "",
            track.releaseDate?: "",
            track.primaryGenreName?: "",
            track.country?: "",
            track.previewUrl?: "",
            track.largeArtworkUrl?: "",
            track.releaseDate?: ""
        )
    }
}