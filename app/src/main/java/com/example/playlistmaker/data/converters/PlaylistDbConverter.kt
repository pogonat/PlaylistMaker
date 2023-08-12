package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.google.gson.Gson

class PlaylistDbConverter(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            trackList = convertToString(playlist.trackList),
            tracksQuantity = playlist.tracksQuantity,
            System.currentTimeMillis()
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            trackList = convertFromJsonToStringList(playlist.trackList),
            tracksQuantity = playlist.tracksQuantity,
            tracksQuantityText = null
        )
    }

    private fun convertToString(trackList: List<String>?): String {
        return when (trackList) {
            null -> ""
            else -> {
                gson.toJson(trackList)
            }
        }
    }

    fun convertFromJsonToStringList(trackList: String): List<String>? {
        return when (trackList) {
            "" -> null
            else -> { gson.fromJson(trackList, Array<String>::class.java).toList()
            }
        }

    }
}