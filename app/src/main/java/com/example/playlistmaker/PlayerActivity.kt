package com.example.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.adapters.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

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

    private var playerState = STATE_DEFAULT
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val gson = App.instance.gson

        val extras = intent.extras
        val stringTrack = extras?.getString(KEY_BUNDLE, "")

        val track = gson.fromJson(stringTrack, Track::class.java)

        mediaPlayer = MediaPlayer()

        initViews()
        fillViews(track)
        preparePlayer(track)

        play.setOnClickListener {
            playbackControl()
        }

        arrowReturn.setOnClickListener {
            finish()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun initViews() {
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

    private fun fillViews(track: Track) {
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

    private fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.getAudioPreviewUrl())
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            Glide.with(this)
                .load(R.drawable.play_track)
                .into(play)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        Glide.with(this)
            .load(R.drawable.track_pause)
            .into(play)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        Glide.with(this)
            .load(R.drawable.play_track)
            .into(play)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        const val KEY_BUNDLE = "KEY_BUNDLE"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}