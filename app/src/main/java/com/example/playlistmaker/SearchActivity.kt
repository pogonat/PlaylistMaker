package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.App.Companion.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.adapters.Track
import com.example.playlistmaker.adapters.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var userInputSearchText = ""

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ITunesSearchApi::class.java)

    private val resultsTracksList = ArrayList<Track>()

    private lateinit var resultsTrackAdapter: TrackAdapter
    private lateinit var historyTrackAdapter: TrackAdapter

    private lateinit var sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPrefs = App.instance.sharedPrefs
        val searchHistory = SearchHistory(sharedPrefs)

        setSearchResultsRecycler(searchHistory)


        setSearchHistoryRecycler(searchHistory, sharedPrefs)

        initViews()

        arrowReturn.setOnClickListener {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
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
            iTunesSearch()
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchInput.text.isNotEmpty()) {
                    iTunesSearch()
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
        historyTrackAdapter = TrackAdapter(searchHistory)
        searchHistory.loadTracksFromJson()
        historyTrackAdapter.tracks = searchHistory.tracksHistory

//        sharedPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener{ _, key ->
//            if (key == SEARCH_HISTORY_KEY) historyTrackAdapter.notifyDataSetChanged()
//        }
//        sharedPrefs.registerOnSharedPreferenceChangeListener(sharedPrefsListener)
        sharedPrefs.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == SEARCH_HISTORY_KEY) historyTrackAdapter.notifyDataSetChanged()
        }

        recyclerHistoryTrackList = findViewById(R.id.recyclerViewSearchHistory)
        recyclerHistoryTrackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerHistoryTrackList.adapter = historyTrackAdapter
    }

    private fun setSearchResultsRecycler(searchHistory: SearchHistory) {
        resultsTrackAdapter = TrackAdapter(searchHistory)
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
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"

        const val SEARCH_HISTORY_KEY = "key_for_search_history"
    }

    private fun iTunesSearch() {
        itunesService
            .search(searchInput.text.toString())
            .enqueue(object : Callback<TracksResponse> {

                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        resultsTracksList.clear()
                        if (response.body()?.searchResults?.isNotEmpty() == true) {
                            resultsTracksList.addAll(response.body()?.searchResults!!)
                            resultsTrackAdapter.notifyDataSetChanged()
                        }
                        if (resultsTracksList.isEmpty()) {
                            negativeResultMessage(NegativeResultMessage.NOTHING_FOUND)
                        }
                    } else {
                        negativeResultMessage(NegativeResultMessage.ERROR_CONNECTION)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    negativeResultMessage(NegativeResultMessage.ERROR_CONNECTION)
                }
            })
    }

    enum class NegativeResultMessage {
        NOTHING_FOUND,
        ERROR_CONNECTION
    }

    private fun negativeResultMessage(errorCode: NegativeResultMessage) {
        errorPlaceholder.visibility = View.VISIBLE
        resultsTracksList.clear()
        resultsTrackAdapter.notifyDataSetChanged()
        when (errorCode) {
            NegativeResultMessage.NOTHING_FOUND -> {
                placeholderMessage.text = getString(R.string.nothing_found)
                Glide.with(placeholderImage)
                    .load(R.drawable.nothing_found)
                    .into(placeholderImage)
            }
            NegativeResultMessage.ERROR_CONNECTION -> {
                placeholderMessage.text = getString(R.string.no_connection)
                Glide.with(placeholderImage)
                    .load(R.drawable.no_connection)
                    .into(placeholderImage)
                renewButton.visibility = View.VISIBLE
            }
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

}

