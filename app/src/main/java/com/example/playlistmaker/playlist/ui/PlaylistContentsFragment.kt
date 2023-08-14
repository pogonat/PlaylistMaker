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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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

    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val trackUIModelList = mutableListOf<TrackUIModel>()

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

        observeViewModel()
        setTracksBottomSheet()
        setMenuBottomSheet()
        setBackNavigation()
        setRecyclerView()
        setButtonListeners()

        viewModel.getPlaylistById(getIdFromArgs())

        viewModel.navigationEvent.observe(viewLifecycleOwner) { intent ->
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        recycleAdapter = null
        _binding = null
    }

    private fun setButtonListeners() {
        setShareCLickListener(binding.shareButton)
        setShareCLickListener(binding.shareMenuButton)

        binding.menuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.editPlaylistButton.setOnClickListener {
            playlist.playlistId?.let {
                findNavController().navigate(
                    R.id.action_playlistContentsFragment_to_playlistEditorFragment,
                    PlaylistEditorFragment.createArgs(it)
                )
            }

        }

        binding.deletePlaylistButton.setOnClickListener {
            val message =
                String.format(getString(R.string.delete_playlist_named), playlist.playlistName)
            val deletePlaylistDialog: MaterialAlertDialogBuilder =
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(message)
                    .setNeutralButton(getString(R.string.answer_negative)) { dialog, which ->
                    }.setPositiveButton(getString(R.string.answer_positive)) { dialog, which ->
                        playlist.playlistId?.let { viewModel.deletePlaylist(it) }
                    }
            deletePlaylistDialog.show()
        }
    }

    private fun setShareCLickListener(view: View) {

        view.setOnClickListener {
            if (trackUIModelList.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    getString(R.string.nothing_to_share),
                    Toast.LENGTH_LONG
                ).show()

            } else {
                val trackQuantityTextFormatted = formatQuantityText(trackUIModelList.size)
                viewModel.sharePlaylist(
                    playlist.playlistId!!,
                    trackQuantityTextFormatted,
                    trackUIModelList
                )
            }
        }
    }

    private fun setRecyclerView() {

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

        recycleAdapter = PlaylistContentsAdapter(
            object : PlaylistContentsAdapter.TrackClickListener {
                override fun onTrackClick(track: TrackUIModel) {
                    onTrackClickDebounce(track)
                }
            },
            object : PlaylistContentsAdapter.TrackLongClickListener {
                override fun onLongTrackClick(track: TrackUIModel) {
                    val confirmDialog = buildDialog(track.trackId)
                    confirmDialog.show()
                }
            }
        )

        binding.apply {
            recyclerViewTracks.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerViewTracks.adapter = recycleAdapter
        }
    }

    private fun setTracksBottomSheet() {

        val bottomSheetContainer = binding.tracksBottomSheet

        tracksBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        tracksBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })
    }

    private fun setMenuBottomSheet() {

        val bottomSheetContainer = binding.menuBottomSheet

        val overlay = binding.overlay

        menuBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
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

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistContentsState.Error -> renderError()
                is PlaylistContentsState.Loading -> showLoading()
                is PlaylistContentsState.PlaylistDeleted -> findNavController().navigateUp()
                is PlaylistContentsState.UpdatePlaylist -> renderTrackListInfo(
                    it.playlistDuration,
                    it.trackList
                )

                is PlaylistContentsState.Content -> showContent(
                    it.playlist,
                    it.trackList,
                    it.playlistDuration
                )
            }
        }
    }

    private fun renderError() {
        binding.progressBar.isVisible = false
        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun showContent(
        foundPlaylist: Playlist,
        foundTracks: List<TrackUIModel>?,
        duration: String?
    ) {
        binding.progressBar.isVisible = false
        playlist = foundPlaylist

        renderPlaylistInfo()
        renderTrackListInfo(duration, foundTracks)
    }

    private fun renderPlaylistInfo() {
        binding.apply {
            playlistTitle.text = playlist.playlistName
            descriptionPlaylist.text = playlist.playlistDescription
            playlistCard.playlistTitle.text = playlist.playlistName
        }
        Glide.with(binding.artworkLarge)
            .load(playlist.imagePath)
            .centerCrop()
            .transform(RoundedCorners(15))
            .placeholder(R.drawable.placeholder_image)
            .into(binding.artworkLarge)

        Glide.with(binding.playlistCard.playlistCover)
            .load(playlist.imagePath)
            .centerCrop()
            .transform(RoundedCorners(15))
            .placeholder(R.drawable.placeholder_image)
            .into(binding.playlistCard.playlistCover)
    }

    private fun renderTrackListInfo(duration: String?, trackList: List<TrackUIModel>?) {

        trackUIModelList.clear()

        binding.playlistDuration.text = formatDurationText(duration)

        if (trackList.isNullOrEmpty()) {
            recycleAdapter?.updateAdapter(emptyList<TrackUIModel>())
            val quantityText = formatQuantityText(0)
            binding. apply {
                trackQuantity.text = quantityText
                tracksBottomSheet.isVisible = false
                playlistCard.tracksQuantity.text = quantityText
                emptyPlaylistMessage.isVisible = true
            }

        } else {
            trackUIModelList.addAll(trackList)
            val quantityText = formatQuantityText(trackList.size)
            binding.trackQuantity.text = quantityText
            binding.playlistCard.tracksQuantity.text = quantityText
            recycleAdapter?.updateAdapter(trackList)
            binding.tracksBottomSheet.isVisible = true
            binding.emptyPlaylistMessage.isVisible = false
        }
    }

    private fun buildDialog(trackId: String): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.delete_track_question))
            .setNeutralButton(getString(R.string.answer_negative)) { dialog, which ->
            }.setPositiveButton(getString(R.string.answer_positive)) { dialog, which ->
                playlist.playlistId?.let { viewModel.deleteTrackFromPlaylist(it, trackId) }
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