package com.example.playlistmaker.player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.data.NetworkSearchImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.TrackStorage
import com.example.playlistmaker.player.domain.AudioPlayerInteractorImpl
import com.example.playlistmaker.search.domain.PlayerState
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.*


class PlayerActivity : AppCompatActivity(), PlayerView {

    private val repository = TrackRepositoryImpl(NetworkSearchImpl(), TrackStorage())
    private val presenter: PlayerPresenter =
        PlayerPresenterImpl(this, AudioPlayerInteractorImpl(), repository)

    private val handler = Handler(Looper.getMainLooper())

    private val setProgressText = Runnable {
        progressTextRenew()
    }

    private lateinit var artwork: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var timeRemained: TextView
    private lateinit var albumCollection: TextView
    private lateinit var year: TextView
    private lateinit var arrowReturn: ImageView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var playlistButton: ImageView
    private lateinit var play: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        presenter.loadTrack()

        play.setOnClickListener {
            presenter.playBackControl()
        }

        arrowReturn.setOnClickListener {
            finish()
        }

    }

    override fun onPause() {
        super.onPause()
        presenter.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgressUpdate()
        presenter.releasePlayer()
    }

    override fun getTrackId(): String {
        val extras = intent.extras
        return extras?.getString(KEY_BUNDLE, "") ?: ""
    }

    override fun initViews() {
        arrowReturn = findViewById(R.id.return_arrow)
        artwork = findViewById(R.id.source_album_art_large)
        trackTitle = findViewById(R.id.trackTitle)
        artistName = findViewById(R.id.artist_name)
        duration = findViewById(R.id.duration)
        timeRemained = findViewById(R.id.time_remained)
        albumCollection = findViewById(R.id.album_collection)
        year = findViewById(R.id.year)
        genre = findViewById(R.id.genre)
        country = findViewById(R.id.country)
        playlistButton = findViewById(R.id.playlist_button)
        play = findViewById(R.id.play_button)
    }

    override fun fillViews(track: Track) {
        trackTitle.text = track.trackName
        artistName.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        timeRemained.text = duration.text
        albumCollection.text = track.collectionName
        year.text = track.getYear()
        genre.text = track.primaryGenreName
        country.text = track.country
        Glide.with(artwork)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(5))
            .placeholder(R.drawable.placeholder_image)
            .into(artwork)
    }

    override fun enablePlayButton() {
        play.isEnabled = true
    }

    override fun updatePlaybackControlButton() {
        val iconRes = if (presenter.getPlayerState() == PlayerState.STATE_PLAYING) {
            R.drawable.pause_track
        } else {
            R.drawable.play_track
        }
        Glide.with(play)
            .load(iconRes)
            .into(play)
    }

    override fun stopProgressUpdate() {
        handler.removeCallbacks(setProgressText)
    }

    override fun progressTextRenew() {
        timeRemained.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(presenter.getCurrentPosition())
        handler.postDelayed(setProgressText, SET_PROGRESS_TEXT_DELAY)
    }

    override fun finishIfTrackNull() {
        Toast.makeText(this, "Can/'t load track info", Toast.LENGTH_SHORT).show()
        finish()
    }


    companion object {
        const val KEY_BUNDLE = "KEY_BUNDLE"
        const val SET_PROGRESS_TEXT_DELAY = 500L
    }
}