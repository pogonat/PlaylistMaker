package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.core.debounce
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import com.example.playlistmaker.media.presentation.models.PlaylistsState
import com.example.playlistmaker.playlist.ui.PlaylistContentsFragment
import com.example.playlistmaker.playlist.ui.adapters.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistsViewModel>()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private var recycleAdapter: PlaylistAdapter? = null

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_playlistCreatorFragment)
        }

        viewModel.getPlaylists()

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistsState.Loading -> showLoading()
                is PlaylistsState.Content -> showContent(it.playlists)
                is PlaylistsState.Error -> showErrorMessage()
            }
        }

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            playlist.playlistId?.let {
                findNavController()
                    .navigate(
                        R.id.action_mediaFragment_to_playlistContentsFragment,
                        PlaylistContentsFragment.createArgs(it)
                    )
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        recycleAdapter = null
        _binding = null
    }

    private fun showContent(playlists: List<Playlist>) {

        val playlistsList = playlists.map { playlist ->
            playlist.copy(tracksQuantityText = formatText(playlist.tracksQuantity))
        }

        recycleAdapter = PlaylistAdapter(
            playlistsList,
            object : PlaylistAdapter.PlaylistClickListener {
                override fun onPlaylistClick(playlist: Playlist) {
                    onPlaylistClickDebounce(playlist)
                }
            }
        )

        binding.apply {

            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.adapter = recycleAdapter

            placeholderErrorImage.isVisible = false
            placeholderMessage.isVisible = false
        }

    }

    private fun showLoading() {
        binding.apply {
            placeholderErrorImage.isVisible = false
            placeholderMessage.isVisible = false
        }
    }

    private fun showErrorMessage() {
        binding.apply {
            placeholderErrorImage.isVisible = true
            placeholderMessage.isVisible = true
        }
    }

    private fun formatText(quantity: Int): String {

        return resources.getQuantityString(R.plurals.tracks_quantity_plurals, quantity, quantity)

    }

    companion object {

        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 500L

        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
}
