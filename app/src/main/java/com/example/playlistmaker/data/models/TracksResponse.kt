package com.example.playlistmaker.data.models

import com.google.gson.annotations.SerializedName

class TracksResponse(@SerializedName("results") val searchResults: ArrayList<TrackDto>) : Response()