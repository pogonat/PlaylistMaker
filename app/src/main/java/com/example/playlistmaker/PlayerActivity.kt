package com.example.playlistmaker

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val gson = App.instance.gson

        val extras = intent.extras
        val stringTrack = extras?.getString(KEY_BUNDLE, "")

        val track = gson.fromJson(stringTrack, Track::class.java)

        initViews()
        fillViews(track)

        arrowReturn.setOnClickListener {
            finish()
        }

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
    }

    private fun fillViews(track: Track) {
        trackTitle.text = track.trackName
        artistName.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        timeRemained.text = duration.text
        albumCollection.text = track.collectionName
        year.text = getYear(track.releaseDate)
        genre.text = track.primaryGenreName
        country.text = track.country
        Glide.with(artwork)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(5))
            .placeholder(R.drawable.placeholder_image)
            .into(artwork)
    }

    private fun getYear(releaseDate: String): String {
        return if (releaseDate.isNotEmpty()) {
            releaseDate.substring(0,4)
        } else ""
    }

    companion object {
        const val KEY_BUNDLE = "KEY_BUNDLE"
    }
}