package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.core.debounce
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.presentation.models.TrackUIModel
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.search.ui.adapters.SearchTracksAdapter
import com.example.playlistmaker.search.presentation.models.SearchScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var onTrackClickDebounce: (TrackUIModel) -> Unit

    private var searchResultsAdapter: SearchTracksAdapter? = null
    private var searchHistoryAdapter: SearchTracksAdapter? = null

    private var userInputSearchText = ""
    private var searchTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null) {
            userInputSearchText = savedInstanceState.getString(SEARCH_TEXT, "")
            binding.inputEditText.setText(userInputSearchText)
        }

        onTrackClickDebounce = debounce<TrackUIModel>(CLICK_DEBOUNCE_DELAY_MILLIS, viewLifecycleOwner.lifecycleScope, false) { track ->
            viewModel.saveItem(track)
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            val trackId = track.trackId
            playerIntent.putExtra(PlayerActivity.KEY_BUNDLE, trackId)
            startActivity(playerIntent)
        }

        searchResultsAdapter = SearchTracksAdapter(
            object : SearchTracksAdapter.TrackClickListener {
                override fun onTrackClick(track: TrackUIModel) {
                    onTrackClickDebounce(track)
                }
            }
        )

        searchHistoryAdapter = SearchTracksAdapter(
            object : SearchTracksAdapter.TrackClickListener {
                override fun onTrackClick(track: TrackUIModel) {
                    onTrackClickDebounce(track)
                }
            }
        )

        setSearchResultsRecycler()
        setSearchHistoryRecycler()

        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchHistory.isVisible = (binding.inputEditText.hasFocus() &&
                        s?.isEmpty() == true && searchHistoryAdapter!!.tracks.isNotEmpty())

                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                userInputSearchText = s?.toString() ?: ""

                viewModel.searchDebounce(changedSearchText = s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchTextWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            userInputSearchText = ""
            viewModel.clearSearchResults()
            binding.searchHistory.isVisible = true
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.clearIcon.windowToken, 0)
        }

        binding.renewButton.setOnClickListener {
            viewModel.searchDebounce(changedSearchText = userInputSearchText)
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.inputEditText.text.isNotEmpty()) {
                    viewModel.searchDebounce(changedSearchText = userInputSearchText)
                }
            }
            false
        }

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty()) {
                viewModel.getTracksHistory()
                binding.searchHistory.isVisible = true
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearTracksHistory()
            binding.searchHistory.isVisible = false
        }

        viewModel.state.observe(viewLifecycleOwner) { screenState ->
            render(screenState)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchResultsAdapter = null
        searchHistoryAdapter = null
        searchTextWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, userInputSearchText)
    }

    private fun setSearchHistoryRecycler() {
        binding.recyclerViewSearchHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewSearchHistory.adapter = searchHistoryAdapter
    }

    private fun setSearchResultsRecycler() {
        binding.recyclerViewResultsItems.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewResultsItems.adapter = searchResultsAdapter
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.ErrorConnection -> showErrorConnection()
            is SearchScreenState.NothingFound -> showNothingFound()
            is SearchScreenState.Success -> showSearchResults(
                state.foundTracks,
                state.historyTracks
            )
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.isVisible = true
            errorPlaceholder.isVisible = false
            searchHistory.isVisible = false
            recyclerViewResultsItems.isVisible = false
        }

    }

    private fun showErrorConnection() {
        binding.apply {
            placeholderMessage.text = getString(R.string.no_connection)
            placeholderErrorImage.setImageResource(R.drawable.no_connection)
            placeholderErrorImage.isVisible = true
            renewButton.isVisible = true

            progressBar.isVisible = false
            errorPlaceholder.isVisible = true
            searchHistory.isVisible = false
            recyclerViewResultsItems.isVisible = false
        }

    }

    private fun showNothingFound() {
        binding.apply {
            placeholderMessage.text = getString(R.string.nothing_found)
            placeholderErrorImage.setImageResource(R.drawable.nothing_found)
            renewButton.isVisible = false

            progressBar.isVisible = false
            errorPlaceholder.isVisible = true
            searchHistory.isVisible = false
            recyclerViewResultsItems.isVisible = false
        }

    }

    private fun showSearchResults(foundTracks: List<TrackUIModel>, historyTracks: List<TrackUIModel>) {
        searchResultsAdapter?.tracks?.clear()
        searchResultsAdapter?.tracks?.addAll(foundTracks)
        searchResultsAdapter?.notifyDataSetChanged()

        searchHistoryAdapter?.tracks?.clear()
        searchHistoryAdapter?.tracks?.addAll(historyTracks)
        searchHistoryAdapter?.notifyDataSetChanged()

        binding.apply {
            progressBar.isVisible = false
            errorPlaceholder.isVisible = false
            searchHistory.isVisible = userInputSearchText.isEmpty() &&
                                      historyTracks.isNotEmpty() &&
                                      binding.inputEditText.hasFocus()
            recyclerViewResultsItems.isVisible = true
        }

    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 300L

    }

}