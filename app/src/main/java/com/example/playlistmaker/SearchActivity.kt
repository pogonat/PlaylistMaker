package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_items)
        recyclerView.layoutManager = LinearLayoutManager(this)

        class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val trackName: TextView = itemView.findViewById(R.id.source_track_name)
            private val artistName: TextView = itemView.findViewById(R.id.source_artist_name)
            private val trackTime: TextView = itemView.findViewById(R.id.source_track_duration)
            private val artworkUrl100: ImageView = itemView.findViewById((R.id.source_album_art))

            fun bind(model: Track) {
                trackName.text = model.trackName
                artistName.text = model.artistName
                trackTime.text = model.trackTime
                Glide.with(artworkUrl100)
                    .load(model.artworkUrl100)
                    .into(artworkUrl100)
            }

        }

        class TrackAdapter(
            private val tracks: ArrayList<Track>
        ) : RecyclerView.Adapter<TracksViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_search_result, parent, false)
                return TracksViewHolder(view)
            }

            override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
                holder.bind(tracks[position])
            }

            override fun getItemCount(): Int {
                return tracks.size
            }
        }

        val trackList: ArrayList<Track> = arrayListOf(
            Track(
                getString(R.string.track_1_trackName),
                getString(R.string.track_1_artistName),
                getString(R.string.track_1_trackTime),
                getString(R.string.track_1_artworkUrl100)
            ),
            Track(
                getString(R.string.track_2_trackName),
                getString(R.string.track_2_artistName),
                getString(R.string.track_2_trackTime),
                getString(R.string.track_2_artworkUrl100)
            ),
            Track(
                getString(R.string.track_3_trackName),
                getString(R.string.track_3_artistName),
                getString(R.string.track_3_trackTime),
                getString(R.string.track_3_artworkUrl100)
            ),
            Track(
                getString(R.string.track_4_trackName),
                getString(R.string.track_4_artistName),
                getString(R.string.track_4_trackTime),
                getString(R.string.track_4_artworkUrl100)
            ),
            Track(
                getString(R.string.track_5_trackName),
                getString(R.string.track_5_artistName),
                getString(R.string.track_5_trackTime),
                getString(R.string.track_5_artworkUrl100)
            )
        )
        val trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter
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

    class Track(
        val trackName: String, // Название композиции
        val artistName: String, // Имя исполнителя
        val trackTime: String, // Продолжительность трека
        val artworkUrl100: String // Ссылка на изображение обложки
    )

}

