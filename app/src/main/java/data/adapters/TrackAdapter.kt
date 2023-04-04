package data.adapters

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.*
import presentation.Activities.PlayerActivity
import presentation.Activities.SearchHistory
import kotlin.collections.ArrayList

class TrackAdapter(private val adapterContext: Context, private val searchHistory: SearchHistory) : RecyclerView.Adapter<TracksViewHolder>() {

    var tracks = ArrayList<Track>()

    private val gson = App.instance.gson

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_view, parent, false)

        return TracksViewHolder(view)

    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {

        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {

            val track = tracks[position]
            searchHistory.saveItem(track)

            if (clickDebounce()) {
                val playerIntent = Intent(adapterContext, PlayerActivity::class.java)
                val jsonTrack = gson.toJson(track)
                playerIntent.putExtra(PlayerActivity.KEY_BUNDLE, jsonTrack)
                adapterContext.startActivity(playerIntent)
            }

        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}