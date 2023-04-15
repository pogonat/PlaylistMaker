package com.example.playlistmaker.data

import com.example.playlistmaker.data.Response
import com.example.playlistmaker.data.TrackDto
import com.google.gson.annotations.SerializedName

class TracksResponse(@SerializedName("results") val searchResults: ArrayList<TrackDto>): Response()