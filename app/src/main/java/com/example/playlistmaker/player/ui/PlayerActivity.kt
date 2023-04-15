package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.models.PlayerScreenState
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.PlayerStatus
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.*


class PlayerActivity : ComponentActivity() {

    private val viewModel by viewModels<PlayerViewModel> { PlayerViewModel.getViewModelFactory() }
    private lateinit var binding: ActivityPlayerBinding

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())


//    private val repository = TrackRepositoryImpl(RetrofitNetworkClient(this), TrackStorageImpl())
//    private val presenter: PlayerPresenter =
//        PlayerViewModel(this, AudioPlayerInteractorImpl(), repository)


    private val setProgressText = Runnable {
        progressTextRenew()
    }

//    private lateinit var artwork: ImageView
//    private lateinit var trackTitle: TextView
//    private lateinit var artistName: TextView
//    private lateinit var duration: TextView
//    private lateinit var timeRemained: TextView
//    private lateinit var albumCollection: TextView
//    private lateinit var year: TextView
//    private lateinit var arrowReturn: ImageView
//    private lateinit var genre: TextView
//    private lateinit var country: TextView
//    private lateinit var playlistButton: ImageView
//    private lateinit var play: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loadTrack(getTrackIdFromIntent())

        binding.playControlButton.setOnClickListener {
            if (clickDebounce()) {viewModel.playBackControl()}
        }

        binding.returnArrow.setOnClickListener {
            finish()
        }

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            render(screenState) }

        viewModel.getPayerStateLiveData().observe(this) { playerState ->
            renderPlayer(playerState) }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgressUpdate()
        viewModel.releasePlayer()
    }

    fun getTrackIdFromIntent(): String {
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

    fun showLoading() {
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
            is PlayerStatus.Paused -> updatePlaybackControlButton()
            is PlayerStatus.Playing -> showPlayerProgress(playerStatus.progress)
        }
    }

    fun showPlayerProgress(progress: Long) {
        val setProgressText = Runnable {
            progressTextRenew()
        }
        binding.timeRemained.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(progress)
        handler.postDelayed(setProgressText, SET_PROGRESS_TEXT_DELAY)
    }

//    override fun initViews() {
//        arrowReturn = findViewById(R.id.returnArrow)
//        artwork = findViewById(R.id.artworkLarge)
//        trackTitle = findViewById(R.id.trackTitle)
//        artistName = findViewById(R.id.artist)
//        duration = findViewById(R.id.duration)
//        timeRemained = findViewById(R.id.timeRemained)
//        albumCollection = findViewById(R.id.albumCollection)
//        year = findViewById(R.id.year)
//        genre = findViewById(R.id.genre)
//        country = findViewById(R.id.country)
//        playlistButton = findViewById(R.id.playlistButton)
//        play = findViewById(R.id.playControlButton)
//    }

    fun enablePlayButton() {
        binding.playControlButton.isEnabled = true
    }

    fun updatePlaybackControlButton() {
        val iconRes = if (viewModel.getPlayerState() == PlayerState.STATE_PLAYING) {
            R.drawable.pause_track
        } else {
            R.drawable.play_track
        }
        Glide.with(binding.playControlButton)
            .load(iconRes)
            .into(binding.playControlButton)
    }

    fun stopProgressUpdate() {
        handler.removeCallbacks(setProgressText)
    }

    fun progressTextRenew() {
        binding.timeRemained.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(viewModel.getCurrentPosition())
        handler.postDelayed(setProgressText, SET_PROGRESS_TEXT_DELAY)
    }

    fun finishIfTrackNull() {
        Toast.makeText(this, "Can/'t load track info", Toast.LENGTH_SHORT).show()
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