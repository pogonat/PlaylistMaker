package data

import data.adapters.Track
import com.google.gson.annotations.SerializedName

class TracksResponse (@SerializedName("results") val searchResults: ArrayList<Track>)