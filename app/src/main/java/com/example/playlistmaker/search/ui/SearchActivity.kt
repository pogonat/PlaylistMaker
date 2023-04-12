package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
                if (clickDebounce()) {
                    viewModel.saveItem(track)
                    val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    val trackId = track.trackId
                    playerIntent.putExtra(PlayerActivity.KEY_BUNDLE, trackId)
                    startActivity(playerIntent)
                }
            }
        }
    )

    setSearchHistoryRecycler(searchHistory, sharedPrefs)


    private var isClickAllowed = true

//    val trackRepository: TrackRepository = TrackRepositoryImpl(NetworkSearchImpl(), TrackStorage())

    //    private val handler = Handler(Looper.getMainLooper())
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

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is SearchScreenState.Success -> {

                }
            }

        }

        viewModel.observeState().observe(this) {
            render(it)
        }

        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.progressBar.isVisible = false
                binding.renewButton.isVisible = false

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(changedSearchText = s?.toString() ?: "")
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                userInputSearchText = s.toString()
                binding.searchHistory.isVisible = (binding.inputEditText.hasFocus() &&
                        s?.isEmpty() == true && viewModel.getTracksHistory().isNotEmpty())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchTextWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

        viewModel.getScreenStateLiveData().setSearchResultsRecycler(searchHistory)



        binding.arrowReturn.setOnClickListener { finish() }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.clearIcon.windowToken, 0)
            resultsTracksList.clear()
            resultsTrackAdapter.notifyDataSetChanged()

        }

        renewButton.setOnClickListener {
            errorPlaceholder.visibility = View.GONE
            renewButton.visibility = View.GONE
            startSearch()
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchInput.text.isNotEmpty()) {
                    startSearch()
                }
            }
            false
        }



        searchInput.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryViewGroup.visibility =
                if (
                    hasFocus &&
                    searchInput.text.isEmpty() &&
                    searchHistory.tracksHistory.isNotEmpty()
                ) View.VISIBLE else View.GONE
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.deleteItems()
            searchHistoryViewGroup.visibility = View.GONE
        }
    }

    private fun changeContentVisibility(loading: Boolean) {
        binding.progressBar.isVisible = loading

        binding.= !loading
        binding..isVisible = !loading
        binding..isVisible = !loading
        binding..isVisible = !loading
    }

    private fun setSearchHistoryRecycler(
        searchHistory: SearchHistory,
        sharedPrefs: SharedPreferences
    ) {
        historyTrackAdapter = SearchTracksAdapter(this, searchHistory)
        searchHistory.loadTracksFromJson()
        historyTrackAdapter.tracks = searchHistory.tracksHistory

        sharedPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == storageHistoryKey) historyTrackAdapter.notifyDataSetChanged()
        }
        sharedPrefs.registerOnSharedPreferenceChangeListener(sharedPrefsListener)

        recyclerHistoryTrackList = findViewById(R.id.recyclerViewSearchHistory)
        recyclerHistoryTrackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerHistoryTrackList.adapter = historyTrackAdapter
    }

    private fun setSearchResultsRecycler(searchHistory: SearchHistory) {
        resultsTrackAdapter = SearchTracksAdapter(this, searchHistory)
        resultsTrackAdapter.tracks = resultsTracksList

        recyclerResultsTrackList = findViewById(R.id.recyclerViewResultsItems)
        recyclerResultsTrackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerResultsTrackList.adapter = resultsTrackAdapter
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

    private fun saveSearchResults(result: SearchTrackResult) {
        when (result.searchResultStatus) {
            SearchResultStatus.SUCCESS -> {
                progressBar.visibility = View.GONE
                resultsTracksList.addAll(result.resultTrackList)
                resultsTrackAdapter.notifyDataSetChanged()
            }
            SearchResultStatus.NOTHING_FOUND -> {
                negativeResultMessage(SearchResultStatus.NOTHING_FOUND)
            }
            SearchResultStatus.ERROR_CONNECTION -> {
                negativeResultMessage(SearchResultStatus.ERROR_CONNECTION)
            }
        }
    }

    private fun startSearch() {
        if (searchInput.text.toString().isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            trackRepository.searchTracks(
                searchInput = searchInput.text.toString(),
                ::saveSearchResults
            )
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun negativeResultMessage(errorCode: SearchResultStatus) {
        progressBar.visibility = View.GONE
        searchHistoryViewGroup.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE

        resultsTracksList.clear()
        resultsTrackAdapter.notifyDataSetChanged()
        when (errorCode) {
            SearchResultStatus.NOTHING_FOUND -> {
                placeholderMessage.text = getString(R.string.nothing_found)
                Glide.with(placeholderImage)
                    .load(R.drawable.nothing_found)
                    .into(placeholderImage)
            }
            SearchResultStatus.ERROR_CONNECTION -> {
                placeholderMessage.text = getString(R.string.no_connection)
                Glide.with(placeholderImage)
                    .load(R.drawable.no_connection)
                    .into(placeholderImage)
                renewButton.visibility = View.VISIBLE
            }
            else -> return
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, userInputSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        userInputSearchText = savedInstanceState.getString(SEARCH_TEXT, "")
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.ErrorConnection -> showErrorConnection()
            is SearchScreenState.NothingFound -> showNothingFound()
            is SearchScreenState.Success -> showSearchResults(state.tracks)
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun showErrorConnection() {
        progressBar.visibility = View.GONE
        negativeResultMessage(SearchResultStatus.ERROR_CONNECTION)
    }

    private fun showNothingFound() {
        progressBar.visibility = View.GONE
        negativeResultMessage(SearchResultStatus.NOTHING_FOUND)
    }

    private fun showSearchResults(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        resultsTracksList.clear()
        resultsTracksList.addAll(tracks)
        resultsTrackAdapter.notifyDataSetChanged()
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
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}

