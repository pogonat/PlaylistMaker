package com.example.playlistmaker

import android.content.Context
import android.content.Intent
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

    private val tracksList = ArrayList<Track>()

    private val trackAdapter = TrackAdapter()

    private lateinit var searchInput: EditText
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var clearButton: ImageView
    private lateinit var renewButton: Button
    private lateinit var arrowReturn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()

        arrowReturn.setOnClickListener {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
        }

        clearButton.setOnClickListener {
            searchInput.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
            tracksList.clear()
            trackAdapter.notifyDataSetChanged()
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
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchInput.addTextChangedListener(searchTextWatcher)

        trackAdapter.tracks = tracksList

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_items)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = trackAdapter
    }

    private fun initView() {
        arrowReturn = findViewById(R.id.arrow_return)
        searchInput = findViewById(R.id.inputEditText)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderErrorImage)
        renewButton = findViewById(R.id.renewButton)
        clearButton = findViewById(R.id.clearIcon)
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
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
                        tracksList.clear()
                        if (response.body()?.searchResults?.isNotEmpty() == true) {
                            tracksList.addAll(response.body()?.searchResults!!)
                            trackAdapter.notifyDataSetChanged()
                        }
                        if (tracksList.isEmpty()) {
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
        tracksList.clear()
        trackAdapter.notifyDataSetChanged()
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

