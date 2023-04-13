package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.SearchTrackResult
import com.example.playlistmaker.search.domain.SearchResultStatus
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.models.SearchScreenState

class SearchActivity : ComponentActivity() {

    private val viewModel by viewModels<SearchViewModel> { SearchViewModel.getViewModelFactory() }
    private lateinit var binding: ActivitySearchBinding

    private val searchResultsAdapter = SearchTracksAdapter(
        object : SearchTracksAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                setOnTrackClickAction(track)
            }
        }
    )

    private val searchHistoryAdapter = SearchTracksAdapter(
        object : SearchTracksAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                setOnTrackClickAction(track)
            }
        }
    )

//    setSearchHistoryRecycler(searchHistory, sharedPrefs)

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
//    val trackRepository: TrackRepository = TrackRepositoryImpl(NetworkSearchImpl(), TrackStorage())


    private var userInputSearchText = ""
    private var searchTextWatcher: TextWatcher? = null
//    private val searchRunnable = Runnable { startSearch() }

//    private val resultsTracksList = ArrayList<Track>()

//    private lateinit var resultsTrackAdapter: TrackAdapter
//    private lateinit var historyTrackAdapter: TrackAdapter

//    private lateinit var sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener
//    private val storageHistoryKey = StorageKeys.SEARCH_HISTORY_KEY.toString()

//    private lateinit var searchInput: EditText
//    private lateinit var errorPlaceholder: LinearLayout
//    private lateinit var placeholderMessage: TextView
//    private lateinit var placeholderImage: ImageView
//    private lateinit var clearButton: ImageView
//    private lateinit var renewButton: Button
//    private lateinit var arrowReturn: ImageView
//    private lateinit var searchHistoryViewGroup: LinearLayout
//    private lateinit var clearHistoryButton: Button
//    private lateinit var recyclerHistoryTrackList: RecyclerView
//    private lateinit var recyclerResultsTrackList: RecyclerView
//    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        viewModel = ViewModelProvider(this, )[SearchViewModel::class.java]
//        val sharedPrefs = App.instance.sharedPrefs
//        val searchHistory = SearchHistory(sharedPrefs)

//        viewModel.observeState().observe(this) {
//            render(it)
//        }
        setSearchResultsRecycler()
        setSearchHistoryRecycler()

        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchHistory.isVisible = (binding.inputEditText.hasFocus() &&
                        s?.isEmpty() == true && viewModel.getTracksHistory().isNotEmpty())

                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                userInputSearchText = s?.toString() ?: ""

                viewModel.searchDebounce(changedSearchText = s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchTextWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.clearIcon.windowToken, 0)

        binding.arrowReturn.setOnClickListener { finish() }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            userInputSearchText = ""
            searchResultsAdapter.tracks.clear()
            searchResultsAdapter.notifyDataSetChanged()
        }

        binding.renewButton.setOnClickListener {
            binding.errorPlaceholder.isVisible = false
            binding.renewButton.isVisible = false

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
                val searchHistory = viewModel.getTracksHistory()
                if (searchHistory.isNotEmpty()) {
                    searchHistoryAdapter.tracks.addAll(searchHistory)
                    searchHistoryAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            searchHistoryAdapter.tracks.clear()
            viewModel.clearTracksHistory()
            searchHistoryAdapter.notifyDataSetChanged()
            binding.searchHistory.isVisible = false
        }

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            render(screenState) }
    }

//    private fun changeContentVisibility(loading: Boolean) {
//        binding.progressBar.isVisible = loading
//
//        binding.= !loading
//        binding..isVisible = !loading
//        binding..isVisible = !loading
//        binding..isVisible = !loading
//    }

    override fun onDestroy() {
        super.onDestroy()
        searchTextWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }

    }

    private fun setSearchHistoryRecycler() {
        binding.recyclerViewSearchHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewSearchHistory.adapter = searchHistoryAdapter

//        searchHistoryAdapter = SearchTracksAdapter(this, searchHistory)
//        searchHistory.loadTracksFromJson()
//        searchHistoryAdapter.tracks = searchHistory.tracksHistory
//
//        sharedPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
//            if (key == storageHistoryKey) searchHistoryAdapter.notifyDataSetChanged()
//        }
//        sharedPrefs.registerOnSharedPreferenceChangeListener(sharedPrefsListener)

//        recyclerViewSearchHistory = findViewById(R.id.recyclerViewSearchHistory)
    }

    private fun setSearchResultsRecycler() {
        binding.recyclerViewResultsItems.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewResultsItems.adapter = searchResultsAdapter

//        resultsTrackAdapter = SearchTracksAdapter(this, searchHistory)
//        searchResultsAdapter.tracks = resultsTracksList

//        binding.recyclerViewResultsItems = findViewById(R.id.recyclerViewResultsItems)

    }

//    private fun initViews() {
//        arrowReturn = findViewById(R.id.arrowReturn)
//        searchInput = findViewById(R.id.inputEditText)
//        errorPlaceholder = findViewById(R.id.errorPlaceholder)
//        placeholderMessage = findViewById(R.id.placeholderMessage)
//        placeholderImage = findViewById(R.id.placeholderErrorImage)
//        renewButton = findViewById(R.id.renewButton)
//        clearButton = findViewById(R.id.clearIcon)
//        searchHistoryViewGroup = findViewById(R.id.searchHistory)
//        clearHistoryButton = findViewById(R.id.clearHistoryButton)
//        progressBar = findViewById(R.id.progressBar)
//    }

//    private fun saveSearchResults(result: SearchTrackResult) {
//        when (result.searchResultStatus) {
//            SearchResultStatus.SUCCESS -> {
//                binding.progressBar.isVisible = false
//                resultsTracksList.addAll(result.resultTrackList)
//                searchResultsAdapter.notifyDataSetChanged()
//            }
//            SearchResultStatus.NOTHING_FOUND -> {
//                negativeResultMessage(SearchResultStatus.NOTHING_FOUND)
//            }
//            SearchResultStatus.ERROR_CONNECTION -> {
//                negativeResultMessage(SearchResultStatus.ERROR_CONNECTION)
//            }
//        }
//    }

//    private fun startSearch(changedSearchText) {
//        viewModel.searchDebounce(changedSearchText)
////        if (changedSearchText.isNotEmpty()) {
////            binding.progressBar.isVisible = true
////            trackRepository.searchTracks(
////                searchInput = searchInput.text.toString(),
////                ::saveSearchResults
////            )
////        }
//    }

//    private fun searchDebounce() {
//        handler.removeCallbacks(searchRunnable)
//        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
//    }

//    private fun negativeResultMessage(errorCode: SearchResultStatus) {
//        binding.progressBar.isVisible = false
//        searchHistoryViewGroup.visibility = View.GONE
//        binding.errorPlaceholder.isVisible = true
//
//        resultsTracksList.clear()
//        searchResultsAdapter.notifyDataSetChanged()
//        when (errorCode) {
//            SearchResultStatus.NOTHING_FOUND -> {
//                binding.placeholderMessage.text = getString(R.string.nothing_found)
//                Glide.with(binding.placeholderErrorImage)
//                    .load(R.drawable.nothing_found)
//                    .into(binding.placeholderErrorImage)
//            }
//            SearchResultStatus.ERROR_CONNECTION -> {
//                binding.placeholderMessage.text = getString(R.string.no_connection)
//                Glide.with(binding.placeholderErrorImage)
//                    .load(R.drawable.no_connection)
//                    .into(binding.placeholderErrorImage)
//                binding.renewButton.isVisible = true
//            }
//            else -> return
//        }
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString(SEARCH_TEXT, userInputSearchText)
//    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        userInputSearchText = savedInstanceState.getString(SEARCH_TEXT, "")
//    }

//    private fun clearButtonVisibility(s: CharSequence?): Int {
//        return if (s.isNullOrEmpty()) {
//            View.GONE
//        } else {
//            View.VISIBLE
//        }
//    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.ErrorConnection -> showErrorConnection()
            is SearchScreenState.NothingFound -> showNothingFound()
            is SearchScreenState.Success -> showSearchResults(state.tracks)
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
        Glide.with(binding.placeholderErrorImage)
            .load(R.drawable.no_connection)
            .into(binding.placeholderErrorImage)
        binding.placeholderErrorImage.isVisible = true
        binding.renewButton.isVisible = true

        binding.progressBar.isVisible = false
        binding.errorPlaceholder.isVisible = true
        binding.searchHistory.isVisible = false
        binding.recyclerViewResultsItems.isVisible = false
    }

    private fun showNothingFound() {
        binding.placeholderMessage.text = getString(R.string.nothing_found)
        Glide.with(binding.placeholderErrorImage)
            .load(R.drawable.nothing_found)
            .into(binding.placeholderErrorImage)
        binding.renewButton.isVisible = false

        binding.progressBar.isVisible = false
        binding.errorPlaceholder.isVisible = true
        binding.searchHistory.isVisible = false
        binding.recyclerViewResultsItems.isVisible = false
    }

    private fun showSearchResults(tracks: List<Track>) {
        searchResultsAdapter.tracks.clear()
        searchResultsAdapter.tracks.addAll(tracks)
        searchResultsAdapter.notifyDataSetChanged()

        binding.progressBar.isVisible = false
        binding.errorPlaceholder.isVisible = false
        binding.searchHistory.isVisible = false
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

    private fun setOnTrackClickAction(track: Track): (Track) -> Unit {
        val funOnClick: (Track) -> Unit =  { track ->
                    if (clickDebounce()) {
                        viewModel.saveItem(track)
                        val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                        val trackId = track.trackId
                        playerIntent.putExtra(PlayerActivity.KEY_BUNDLE, trackId)
                        startActivity(playerIntent)
                    }
                }
        return funOnClick
    }

    companion object {
//        const val SEARCH_TEXT = "SEARCH_TEXT"
//        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}

