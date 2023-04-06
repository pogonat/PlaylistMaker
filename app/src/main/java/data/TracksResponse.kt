package data

import data.models.Track
import com.google.gson.annotations.SerializedName

class TracksResponse (@SerializedName("results") val searchResults: ArrayList<Track>)