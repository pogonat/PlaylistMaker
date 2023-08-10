package com.example.playlistmaker.presentation.models

import com.example.playlistmaker.core.DateTimeUtil
import com.example.playlistmaker.domain.models.Track

class TrackToTrackUIModelConverter {

    fun map(track: Track): TrackUIModel {
        return TrackUIModel(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            trackDurationFormatted =  DateTimeUtil.formatDurationMillisToTime(track.trackTimeMillis),
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            largeArtworkUrl = track.largeArtworkUrl,
            collectionYear = DateTimeUtil.formatCollectionYear(track.releaseDate),
            isFavourite = track.isFavourite
        )
    }

    fun map(trackUIModel: TrackUIModel): Track {
        return Track(
            trackId = trackUIModel.trackId,
            trackName = trackUIModel.trackName,
            artistName = trackUIModel.artistName,
            trackTimeMillis = trackUIModel.trackTimeMillis,
            artworkUrl100 = trackUIModel.artworkUrl100,
            collectionName = trackUIModel.collectionName,
            releaseDate = trackUIModel.releaseDate,
            primaryGenreName = trackUIModel.primaryGenreName,
            country = trackUIModel.country,
            previewUrl = trackUIModel.previewUrl,
            largeArtworkUrl = trackUIModel.largeArtworkUrl
        )
    }

    fun mapListToTrackUIModels(listTracks: List<Track>): List<TrackUIModel> {
        val trackUIModelsList = ArrayList<TrackUIModel>()

        for (track in listTracks) {
            val trackUIModel = map(track)
            trackUIModelsList.add(trackUIModel)
        }

        return trackUIModelsList
    }

}
