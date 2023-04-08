package com.example.playlistmaker.data.models

import com.google.gson.annotations.SerializedName
import com.example.playlistmaker.data.models.TrackDto

class TracksResponse (@SerializedName("results")  val searchResults: ArrayList<TrackDto>)