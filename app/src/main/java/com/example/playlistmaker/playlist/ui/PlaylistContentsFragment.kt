package com.example.playlistmaker.playlist.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.core.debounce
import com.example.playlistmaker.databinding.FragmentPlaylistcontentsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.playlist.presentation.PlaylistContentsViewModel
import com.example.playlistmaker.playlist.presentation.models.PlaylistContentsState
import com.example.playlistmaker.playlist.ui.adapters.PlaylistContentsAdapter
import com.example.playlistmaker.presentation.models.TrackUIModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistContentsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistContentsViewModel>()

    private var _binding: FragmentPlaylistcontentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlist: Playlist

    private lateinit var onTrackClickDebounce: (TrackUIModel) -> Unit

    private var recycleAdapter: PlaylistContentsAdapter? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistcontentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onTrackClickDebounce = debounce<TrackUIModel>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            val trackId = track.trackId
            playerIntent.putExtra(PlayerActivity.KEY_BUNDLE, trackId)
            startActivity(playerIntent)
        }

        viewModel.getPlaylistById(getIdFromArgs())

        setBottomSheet()
        setBackNavigation()

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistContentsState.Error -> renderError()
                is PlaylistContentsState.Loading -> showLoading()
                is PlaylistContentsState.Content -> showContent(
                    it.playlist,
                    it.trackList,
                    it.playlistDuration
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recycleAdapter = null
        _binding = null
    }

    private fun setBottomSheet() {

        val bottomSheetContainer = binding.tracksBottomSheet

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })
    }

    private fun renderError() {
        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun showLoading() {}

    private fun showContent(
        foundPlaylist: Playlist,
        foundTracks: List<TrackUIModel>?,
        duration: String?
    ) {
        playlist = foundPlaylist
        foundTracks?.let {
            showTracks(foundTracks)
        }

        binding.apply {
            playlistTitle.text = playlist.playlistName
            descriptionPlaylist.text = playlist.playlistDescription
            playlistDuration.text = formatDurationText(duration)
            trackQuantity.text = formatQuantityText(playlist.tracksQuantity)
        }
    }

    private fun showTracks(foundTracks: List<TrackUIModel>) {
        recycleAdapter = PlaylistContentsAdapter(
            foundTracks,
            object : PlaylistContentsAdapter.TrackClickListener {
                override fun onTrackClick(track: TrackUIModel) {
                    onTrackClickDebounce(track)
                }
            },
            object : PlaylistContentsAdapter.TrackLongClickListener {
                override fun onLongTrackClick(track: TrackUIModel) {
                    buildDialog(track.trackId)
                }
            }
        )

        binding.apply {
            recyclerViewTracks.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerViewTracks.adapter = recycleAdapter
            tracksBottomSheet.isVisible = true
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

    }

    private fun setBackNavigation() {

        binding.returnArrow.setOnClickListener {
            findNavController().navigateUp()

        }

        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
    }

    private fun buildDialog(trackId: String) {
        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.delete_track_question))
            .setNeutralButton(getString(R.string.answer_negative)) { dialog, which ->
            }.setPositiveButton(getString(R.string.answer_positive)) { dialog, which ->
                TODO()
            }
    }

    private fun formatDurationText(durationText: String?): String {
        return if (durationText.isNullOrEmpty()) {
            resources.getQuantityString(R.plurals.playlist_duration_plurals, 0, 0)
        } else {
            val intDuration = durationText.toInt()
            resources.getQuantityString(
                R.plurals.playlist_duration_plurals,
                intDuration,
                intDuration
            )
        }
    }

    private fun formatQuantityText(quantity: Int): String {
        return resources.getQuantityString(
            R.plurals.tracks_quantity_plurals,
            quantity,
            quantity
        )
    }

    private fun getIdFromArgs(): Int {
        return requireArguments().getInt(ARGS_PLAYLIST_ID)
    }

    companion object {

        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val ARGS_PLAYLIST_ID = "ARGS_PLAYLIST_ID"
        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }

}