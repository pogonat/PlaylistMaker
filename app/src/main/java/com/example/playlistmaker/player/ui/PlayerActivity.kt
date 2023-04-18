package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.models.PlayerScreenState
import com.example.playlistmaker.player.ui.models.PlayerStatus
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.*


class PlayerActivity : AppCompatActivity() {

    private val viewModel by viewModels<PlayerViewModel> { PlayerViewModel.getViewModelFactory() }
    private lateinit var binding: ActivityPlayerBinding

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val setProgressText = Runnable {
        progressTextRenew()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loadTrack(getTrackIdFromIntent())

        binding.playControlButton.setOnClickListener {
            if (clickDebounce()) {
                handler.removeCallbacks(setProgressText)
                viewModel.playBackControl()
            }
        }

        binding.returnArrow.setOnClickListener {
            finish()
        }

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            render(screenState)
        }

        viewModel.getPayerStateLiveData().observe(this) { playerState ->
            renderPlayer(playerState)
        }

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(setProgressText)
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(setProgressText)
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

    private fun showPlayerProgress(progress: Int) {
        binding.playControlButton.setImageResource(R.drawable.pause_track)
        binding.timeRemained.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(progress)
        handler.postDelayed(setProgressText, SET_PROGRESS_TEXT_DELAY)
    }

    private fun enablePlayButton() {
        binding.playControlButton.isEnabled = true
    }

    private fun updatePlayerButton() {
        handler.removeCallbacks(setProgressText)
        binding.playControlButton.setImageResource(R.drawable.play_track)
    }

    private fun stopProgressUpdate() {
        handler.removeCallbacks(setProgressText)

        binding.playControlButton.setImageResource(R.drawable.play_track)

        binding.timeRemained.text = binding.duration.text
    }

    private fun progressTextRenew() {
        viewModel.getCurrentPosition()
    }

    private fun finishIfTrackNull() {
        Toast.makeText(this, "Can\'t load track info", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val KEY_BUNDLE = "KEY_BUNDLE"
        private const val SET_PROGRESS_TEXT_DELAY = 500L
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}