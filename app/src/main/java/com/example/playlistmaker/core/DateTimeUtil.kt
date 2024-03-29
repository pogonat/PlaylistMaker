package com.example.playlistmaker.core

import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {

    private const val YEAR_INDEX_STARTING = 0
    private const val YEAR_INDEX_ENDING = 4

    fun formatDurationMillisToTime(trackTime: String?): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(trackTime?.toLong() ?: 0f)
    }

    fun formatDurationMillisToTime(durationMillis: Long): String {
        return SimpleDateFormat(
            "mm",
            Locale.getDefault()
        ).format(durationMillis)
    }

    fun formatCollectionYear(releaseDate: String?): String {
        return releaseDate?.substring(YEAR_INDEX_STARTING, YEAR_INDEX_ENDING) ?: ""
    }

}