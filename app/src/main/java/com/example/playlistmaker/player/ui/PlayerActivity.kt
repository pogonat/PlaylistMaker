package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.player.presentation.models.PlayerStatus
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.player.presentation.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerActivity : AppCompatActivity() {

    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loadTrack(getTrackIdFromIntent())

        binding.playControlButton.setOnClickListener {
            viewModel.playBackControl()
        }

        binding.returnArrow.setOnClickListener {
            finish()
        }

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            render(screenState)
        }

        viewModel.getPlayerStateLiveData().observe(this) { playerState ->
            renderPlayer(playerState)
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun getTrackIdFromIntent(): String {
        val extras = intent.extras
        return extras?.getString(KEY_BUNDLE, "") ?: ""
    }

    private fun render(state: PlayerScreenState) {
        when (state) {
            is PlayerScreenState.Loading -> showLoading()
            is PlayerScreenState.Content -> showContent(state.track)
            is PlayerScreenState.Destroy -> finishIfTrackNull()
        }
    }

    private fun showLoading() {
        Toast.makeText(this, "Loading Track", Toast.LENGTH_SHORT).show()
    }

    private fun showContent(track: Track) {
        binding.trackTitle.text = track.trackName
        binding.artist.text = track.artistName
        binding.duration.text = track.trackTime
        binding.timeRemained.text = binding.duration.text
        binding.albumCollection.text = track.collectionName
        binding.year.text = track.collectionYear
        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country
        Glide.with(binding.artworkLarge)
            .load(track.largeArtworkUrl)
            .centerCrop()
            .transform(RoundedCorners(5))
            .placeholder(R.drawable.placeholder_image)
            .into(binding.artworkLarge)
    }

    private fun renderPlayer(playerStatus: PlayerStatus) {
        when (playerStatus) {
            is PlayerStatus.Ready -> enablePlayButton()
            is PlayerStatus.Paused -> updatePlayerButton()
            is PlayerStatus.Playing -> showPlayerProgress(playerStatus.progress)
            is PlayerStatus.Complete -> stopProgressUpdate()
        }
    }

    private fun showPlayerProgress(progress: String) {
        binding.playControlButton.setImageResource(R.drawable.pause_track)
        binding.timeRemained.text = progress
    }

    private fun enablePlayButton() {
        binding.playControlButton.isEnabled = true
    }

    private fun updatePlayerButton() {
        binding.playControlButton.setImageResource(R.drawable.play_track)
    }

    private fun stopProgressUpdate() {

        binding.playControlButton.setImageResource(R.drawable.play_track)

        binding.timeRemained.text = binding.duration.text
    }

    private fun finishIfTrackNull() {
        Toast.makeText(this, "Can\'t load track info", Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val KEY_BUNDLE = "KEY_BUNDLE"
    }
}