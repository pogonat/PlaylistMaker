package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.adapters.SearchTracksAdapter
import com.example.playlistmaker.search.ui.models.SearchScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var binding: FragmentSearchBinding

    private val searchResultsAdapter = SearchTracksAdapter(
        object : SearchTracksAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    viewModel.saveItem(track)
                    val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
                    val trackId = track.trackId
                    playerIntent.putExtra(PlayerActivity.KEY_BUNDLE, trackId)
                    startActivity(playerIntent)
                }
            }
        }
    )

    private val searchHistoryAdapter = SearchTracksAdapter(
        object : SearchTracksAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    viewModel.saveItem(track)
                    val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
                    val trackId = track.trackId
                    playerIntent.putExtra(PlayerActivity.KEY_BUNDLE, trackId)
                    startActivity(playerIntent)
                }
            }
        }
    )

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())


    private var userInputSearchText = ""
    private var searchTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSearchResultsRecycler()
        setSearchHistoryRecycler()

        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchHistory.isVisible = (binding.inputEditText.hasFocus() &&
                        s?.isEmpty() == true && searchHistoryAdapter.tracks.isNotEmpty())

                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                userInputSearchText = s?.toString() ?: ""

                viewModel.searchDebounce(changedSearchText = s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchTextWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

//        binding.arrowReturn.setOnClickListener { finish() }

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

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            render(screenState)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchTextWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, userInputSearchText)
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        userInputSearchText = savedInstanceState.getString(SEARCH_TEXT, "")
//    }

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
        binding.progressBar.isVisible = true
        binding.errorPlaceholder.isVisible = false
        binding.searchHistory.isVisible = false
        binding.recyclerViewResultsItems.isVisible = false
    }

    private fun showErrorConnection() {
        binding.placeholderMessage.text = getString(R.string.no_connection)
        binding.placeholderErrorImage.setImageResource(R.drawable.no_connection)
        binding.placeholderErrorImage.isVisible = true
        binding.renewButton.isVisible = true

        binding.progressBar.isVisible = false
        binding.errorPlaceholder.isVisible = true
        binding.searchHistory.isVisible = false
        binding.recyclerViewResultsItems.isVisible = false
    }

    private fun showNothingFound() {
        binding.placeholderMessage.text = getString(R.string.nothing_found)
        binding.placeholderErrorImage.setImageResource(R.drawable.nothing_found)
        binding.renewButton.isVisible = false

        binding.progressBar.isVisible = false
        binding.errorPlaceholder.isVisible = true
        binding.searchHistory.isVisible = false
        binding.recyclerViewResultsItems.isVisible = false
    }

    private fun showSearchResults(foundTracks: List<Track>, historyTracks: List<Track>) {
        searchResultsAdapter.tracks.clear()
        searchResultsAdapter.tracks.addAll(foundTracks)
        searchResultsAdapter.notifyDataSetChanged()
        searchHistoryAdapter.tracks.clear()
        searchHistoryAdapter.tracks.addAll(historyTracks)
        searchHistoryAdapter.notifyDataSetChanged()


        binding.progressBar.isVisible = false
        binding.errorPlaceholder.isVisible = false
        binding.searchHistory.isVisible =
            (userInputSearchText == "" && historyTracks.isNotEmpty() && binding.inputEditText.hasFocus())
        binding.recyclerViewResultsItems.isVisible = true
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
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }


}