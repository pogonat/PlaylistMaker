package com.example.playlistmaker.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.data.NetworkSearchImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.TrackStorage
import com.example.playlistmaker.presentation.SearchHistory
import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.StorageKeys
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.adapters.TrackAdapter

class SearchActivity : AppCompatActivity() {

    val trackRepository: TrackRepository = TrackRepositoryImpl(NetworkSearchImpl(), TrackStorage())

    private val handler = Handler(Looper.getMainLooper())

    private var userInputSearchText = ""

    private val searchRunnable = Runnable { startSearch() }

    private val resultsTracksList = ArrayList<Track>()

    private lateinit var resultsTrackAdapter: TrackAdapter
    private lateinit var historyTrackAdapter: TrackAdapter

    private lateinit var sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener
    private val storageHistoryKey = StorageKeys.SEARCH_HISTORY_KEY.toString()

    private lateinit var searchInput: EditText
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var clearButton: ImageView
    private lateinit var renewButton: Button
    private lateinit var arrowReturn: ImageView
    private lateinit var searchHistoryViewGroup: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var recyclerHistoryTrackList: RecyclerView
    private lateinit var recyclerResultsTrackList: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPrefs = App.instance.sharedPrefs
        val searchHistory = SearchHistory(sharedPrefs)


        setSearchResultsRecycler(searchHistory)

        setSearchHistoryRecycler(searchHistory, sharedPrefs)

        initViews()

        arrowReturn.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchInput.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
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

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                errorPlaceholder.visibility = View.GONE
                renewButton.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                clearButton.visibility = clearButtonVisibility(s)
                userInputSearchText = s.toString()
                searchHistoryViewGroup.visibility =
                    if (
                        searchInput.hasFocus() &&
                        s?.isEmpty() == true &&
                        searchHistory.tracksHistory.isNotEmpty()
                    ) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchInput.addTextChangedListener(searchTextWatcher)

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

    private fun setSearchHistoryRecycler(
        searchHistory: SearchHistory,
        sharedPrefs: SharedPreferences
    ) {
        historyTrackAdapter = TrackAdapter(this, searchHistory)
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
        resultsTrackAdapter = TrackAdapter(this, searchHistory)
        resultsTrackAdapter.tracks = resultsTracksList

        recyclerResultsTrackList = findViewById(R.id.recyclerViewResultsItems)
        recyclerResultsTrackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerResultsTrackList.adapter = resultsTrackAdapter
    }

    private fun initViews() {
        arrowReturn = findViewById(R.id.arrowReturn)
        searchInput = findViewById(R.id.inputEditText)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderErrorImage)
        renewButton = findViewById(R.id.renewButton)
        clearButton = findViewById(R.id.clearIcon)
        searchHistoryViewGroup = findViewById(R.id.searchHistory)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)
    }

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
        progressBar.visibility = View.VISIBLE
        trackRepository.searchTracks(searchInput = searchInput.text.toString(), ::saveSearchResults)

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

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}

