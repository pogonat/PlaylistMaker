package com.example.playlistmaker.search.data

import com.google.gson.annotations.SerializedName

class TracksResponse(@SerializedName("results") val searchResults: ArrayList<TrackDto>)