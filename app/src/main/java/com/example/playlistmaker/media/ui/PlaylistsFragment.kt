package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import com.example.playlistmaker.media.presentation.models.PlaylistsState
import com.example.playlistmaker.playlist.ui.adapters.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistsViewModel>()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private var recycleAdapter: PlaylistAdapter? = null

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

        viewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistsState.Loading -> showLoading()
                is PlaylistsState.Content -> showContent(it.playlists)
                is PlaylistsState.Error -> showErrorMessage()
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
        recycleAdapter = PlaylistAdapter(playlistsList)

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
        val lastDigit = quantity % 10
        val quantityText = requireContext().getString(R.string.track_quantity)
        val stringDefault = requireContext().getString(R.string.track_quantity_default)
        val stringFew = requireContext().getString(R.string.track_quantity_few)
        val stringMany = requireContext().getString(R.string.track_quantity_many)
        return when (lastDigit) {
            1 -> String.format(quantityText, lastDigit, stringDefault)
            2, 3, 4 -> String.format(quantityText, lastDigit, stringFew)
            else -> String.format(quantityText, lastDigit, stringMany)
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
