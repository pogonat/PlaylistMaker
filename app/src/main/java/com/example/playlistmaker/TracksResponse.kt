package com.example.playlistmaker

import com.example.playlistmaker.adapters.Track
import com.google.gson.annotations.SerializedName

class TracksResponse (@SerializedName("results") val searchResults: ArrayList<Track>)