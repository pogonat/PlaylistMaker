package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.core.debounce
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.media.presentation.models.FavouritesState
import com.example.playlistmaker.media.presentation.FavouritesViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.adapters.SearchTracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment : Fragment() {

    private val viewModel by viewModel<FavouritesViewModel>()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var favListAdapter: SearchTracksAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            val trackId = track.trackId
            playerIntent.putExtra(PlayerActivity.KEY_BUNDLE, trackId)
            startActivity(playerIntent)
        }

        favListAdapter = SearchTracksAdapter(
            object : SearchTracksAdapter.TrackClickListener {
                override fun onTrackClick(track: Track) {
                    onTrackClickDebounce(track)
                }
            }
        )

        binding.recyclerViewFavourites.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewFavourites.adapter = favListAdapter

        viewModel.getFavouritesList()

        viewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is FavouritesState.Loading -> showErrorMessage()
                is FavouritesState.Content -> renderList(it.favList)
                is FavouritesState.Error -> showErrorMessage()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        favListAdapter = null
        _binding = null
    }

    private fun renderList(favList: List<Track>) {
        favListAdapter?.tracks?.clear()
        favListAdapter?.tracks?.addAll(favList)
        favListAdapter?.notifyDataSetChanged()

        binding.apply {
            placeholderErrorImage.isVisible = false
            placeholderMessage.isVisible = false
            recyclerViewFavourites.isVisible = true
        }
    }

    private fun showErrorMessage() {
        binding.apply {
            recyclerViewFavourites.isVisible = false
            placeholderErrorImage.isVisible = true
            placeholderMessage.isVisible = true
        }
    }

    companion object {
        fun newInstance() = FavouritesFragment()
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L

    }
}