package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.models.TracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkSearchItunesApi {

    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): TracksResponse

    @GET("/search?entity=song")
    suspend fun getTrackDetails(@Query("term") trackId: String): TracksResponse
}