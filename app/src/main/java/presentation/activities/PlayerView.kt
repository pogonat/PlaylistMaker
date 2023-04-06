package presentation.activities

import data.models.Track

interface PlayerView {
    fun getTrack(): String
    fun initViews()
    fun fillViews(track: Track)
    fun enablePlayButton()
    fun updatePlaybackControlButton()
    fun stopProgressUpdate()
}