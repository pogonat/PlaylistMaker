package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {

    private var userInputSearchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrowReturn = findViewById<ImageView>(R.id.arrow_return)

        arrowReturn.setOnClickListener {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                userInputSearchText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
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

    class Track (
        val trackName: String, // Название композиции
        val artistName: String, // Имя исполнителя
        val trackTime: String, // Продолжительность трека
        val artworkUrl100: String // Ссылка на изображение обложки
    )

    val trackList: ArrayList<Track> = arrayListOf(
        Track(
            getString(R.string.track_1_trackName),
            getString(R.string.track_1_artistName),
            getString(R.string.track_1_trackTime),
            getString(R.string.track_1_artworkUrl100)),
        Track(getString(R.string.track_2_trackName),
            getString(R.string.track_2_artistName),
            getString(R.string.track_2_trackTime),
            getString(R.string.track_2_artworkUrl100)),
        Track(getString(R.string.track_3_trackName),
            getString(R.string.track_3_artistName),
            getString(R.string.track_3_trackTime),
            getString(R.string.track_3_artworkUrl100)),
        Track(getString(R.string.track_4_trackName),
            getString(R.string.track_4_artistName),
            getString(R.string.track_4_trackTime),
            getString(R.string.track_4_artworkUrl100)),
        Track(getString(R.string.track_5_trackName),
            getString(R.string.track_5_artistName),
            getString(R.string.track_5_trackTime),
            getString(R.string.track_5_artworkUrl100)
        )
    )



}

