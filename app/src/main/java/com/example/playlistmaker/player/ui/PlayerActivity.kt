package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.player.presentation.models.PlayerStatus
import com.example.playlistmaker.media.ui.adapters.PlaylistPlayerAdapter
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.playlist.ui.PlaylistCreatorFragment
import com.example.playlistmaker.presentation.models.TrackUIModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var binding: ActivityPlayerBinding

    private lateinit var track: TrackUIModel

    private var recycleAdapter: PlaylistPlayerAdapter? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTrack()

        binding.playControlButton.setOnClickListener {
            viewModel.playBackControl()
        }

        binding.returnArrow.setOnClickListener {
            finish()
        }

        binding.favlistButton.setOnClickListener {
            viewModel.toggleFavouriteDebounce(track)
        }

        binding.playlistButton.setOnClickListener {
            viewModel.clickPlaylistDebounce()
        }

        setBottomSheet()

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
        recycleAdapter = null
        viewModel.releasePlayer()
    }

    private fun loadTrack() {
        val trackId = getTrackIdFromIntent()
        if (trackId.isEmpty()) {
            finish()
        } else {
            viewModel.loadTrack(trackId)
        }
    }

    private fun getTrackIdFromIntent(): String {
        val extras = intent.extras
        return extras?.getString(KEY_BUNDLE, "") ?: ""
    }

    private fun setBottomSheet() {

        val bottomSheetContainer = binding.playlistsBottomSheet

        val overlay = binding.overlay

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.isVisible = false
                    }

                    else -> {
                        overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset
            }
        })

        binding.newPlaylistButton.setOnClickListener {

            val playlistCreationFragment = PlaylistCreatorFragment()
            val argTrackId = Bundle()
            argTrackId.putString(KEY_BUNDLE, track.trackId)
            playlistCreationFragment.arguments = argTrackId

            binding.scrollView.isVisible = false
            binding.playlistsBottomSheet.isVisible = false
            binding.overlay.isVisible = false

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, playlistCreationFragment)
                .commit()
        }
    }

    private fun render(state: PlayerScreenState) {
        when (state) {
            is PlayerScreenState.Loading -> showLoading()
            is PlayerScreenState.Content -> showContent(state.track)
            is PlayerScreenState.Destroy -> finishIfTrackNull()
            is PlayerScreenState.BottomSheet -> showBottomSheet(state.playlist)
            is PlayerScreenState.BottomSheetHidden -> hideBottomSheet(state.playlistTitle)
            is PlayerScreenState.ShowMessage -> processMessage(state.playlistTitle)
        }
    }

    private fun processMessage(playlistTitle: String?) {
        when (playlistTitle) {
            null -> {
                Toast
                    .makeText(this, getString(R.string.playlist_update_error), Toast.LENGTH_LONG)
                    .show()
            }
            else -> {
                showMessage(getString(R.string.add_track_error), playlistTitle)
            }
        }
    }

    private fun hideBottomSheet(playlistTitle: String) {
        showMessage(getString(R.string.track_added), playlistTitle)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    }

    private fun showMessage(messageString: String, playlistTitle: String) {
        val message = String.format(messageString, playlistTitle)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showBottomSheet(playlists: List<Playlist>) {

        val playlistsList = playlists.map { playlist ->
            playlist.copy(tracksQuantityText = formatText(playlist.tracksQuantity))
        }

        recycleAdapter = PlaylistPlayerAdapter(
            playlistsList,
            object : PlaylistPlayerAdapter.PlaylistClickListener {
                override fun onPlaylistClick(playlist: Playlist) {
                    viewModel.addTrackToPlaylist(playlist, track)
                }
            }
        )

        binding.apply {

            recyclerViewPlaylists.layoutManager =
                LinearLayoutManager(this@PlayerActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewPlaylists.adapter = recycleAdapter

        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun showContent(foundTrack: TrackUIModel) {
        track = foundTrack
        binding.apply {
            progressBar.isVisible = false
            trackTitle.text = foundTrack.trackName
            artist.text = foundTrack.artistName
            duration.text = foundTrack.trackDurationFormatted
            timeRemained.text = binding.duration.text
            albumCollection.text = foundTrack.collectionName
            year.text = foundTrack.collectionYear
            genre.text = foundTrack.primaryGenreName
            country.text = foundTrack.country
        }
        Glide.with(binding.artworkLarge)
            .load(foundTrack.largeArtworkUrl)
            .centerCrop()
            .transform(RoundedCorners(15))
            .placeholder(R.drawable.placeholder_image)
            .into(binding.artworkLarge)

        if (track.isFavourite) {
            binding.favlistButton.setImageResource(R.drawable.remove_favlist_icon)
        } else {
            binding.favlistButton.setImageResource(R.drawable.add_favlist_icon)
        }

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

        binding.timeRemained.text = getString(R.string.player_completed)
    }

    private fun finishIfTrackNull() {
        Toast.makeText(this, "Can\'t load track info", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun formatText(quantity: Int): String {

        return resources.getQuantityString(R.plurals.tracks_quantity_plurals, quantity, quantity)

    }

    companion object {
        const val KEY_BUNDLE = "TRACK_ID"
    }
}