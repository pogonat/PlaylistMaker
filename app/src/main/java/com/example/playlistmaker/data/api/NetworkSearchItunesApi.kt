package com.example.playlistmaker.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.playlistmaker.data.models.TracksResponse

interface NetworkSearchItunesApi {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TracksResponse>

    @GET("/search?entity=song")
    fun getTrackDetails(@Query("term") trackId: String): Call<TracksResponse>
}