package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.dao.TracksInPlaylistsDao
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TracksInPlaylistsEntity

@Database(version = 4, entities = [TrackEntity::class, PlaylistEntity::class, TracksInPlaylistsEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playListDao(): PlaylistDao

    abstract  fun tracksInPlaylistsDao(): TracksInPlaylistsDao

}