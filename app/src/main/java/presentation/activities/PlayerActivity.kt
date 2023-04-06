package presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import data.models.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity(), PlayerView {
    private val presenter: PlayerPresenter = PlayerPresenterImpl(this, AudioPlayer())

    private val handler = Handler(Looper.getMainLooper())

    private val setProgressText = Runnable {
        progressTextRenew()
    }

    private lateinit var artwork: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var timeRemained: TextView
    private lateinit var albumCollection: TextView
    private lateinit var year: TextView
    private lateinit var arrowReturn: ImageView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var playlistButton: ImageView
    private lateinit var play: ImageView

//    private var playerState = STATE_DEFAULT
//    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        presenter.loadTrack()

        val track = getTrack()

//        mediaPlayer = MediaPlayer()

//        initViews()
//        fillViews(track)
//        preparePlayer(track)

        play.setOnClickListener {
            presenter.playBackControl()
        }

        arrowReturn.setOnClickListener {
            finish()
        }

    }

    override fun onPause() {
        super.onPause()
        presenter.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgressUpdate()
        presenter.releasePlayer()
    }

    override fun getTrack(): String {
        val extras = intent.extras
        return extras?.getString(KEY_BUNDLE, "") ?: ""
    }

    override fun initViews() {
        arrowReturn = findViewById(R.id.return_arrow)
        artwork = findViewById(R.id.source_album_art_large)
        trackTitle = findViewById(R.id.trackTitle)
        artistName = findViewById(R.id.artist_name)
        duration = findViewById(R.id.duration)
        timeRemained = findViewById(R.id.time_remained)
        albumCollection = findViewById(R.id.album_collection)
        year = findViewById(R.id.year)
        genre = findViewById(R.id.genre)
        country = findViewById(R.id.country)
        playlistButton = findViewById(R.id.playlist_button)
        play = findViewById(R.id.play_button)
    }

    override fun fillViews(track: Track) {
        trackTitle.text = track.trackName
        artistName.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        timeRemained.text = duration.text
        albumCollection.text = track.collectionName
        year.text = track.getYear()
        genre.text = track.primaryGenreName
        country.text = track.country
        Glide.with(artwork)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(5))
            .placeholder(R.drawable.placeholder_image)
            .into(artwork)
    }

//    private fun preparePlayer(track: Track) {
//        presenter.preparePlayer(track.getAudioPreviewUrl(),{
//            updatePlaybackControlButton()// обновить кнопку setOnPreparedListener
//        }, {
//            updatePlaybackControlButton()// обновить кнопку и убрать колбэки setOnCompletionListener
//        })
//        mediaPlayer.setDataSource(track.getAudioPreviewUrl())
//        mediaPlayer.prepareAsync()
//        mediaPlayer.setOnPreparedListener {
//            play.isEnabled = true
////            playerState = STATE_PREPARED
//        }
//        mediaPlayer.setOnCompletionListener {
//            handler.removeCallbacks(setProgressText)
////            playerState = STATE_PREPARED
//            timeRemained.text = "00:00"
//            Glide.with(play)
//                .load(R.drawable.play_track)
//                .into(play)
//        }
//    }

//    override fun startPlayer() {
//        presenter.startPlayer()
////        mediaPlayer.start()
////        playerState = STATE_PLAYING
//        progressTextRenew()
//        updatePlaybackControlButton()
////        Glide.with(play)
////            .load(R.drawable.pause_track)
////            .into(play)
//    }

//    override fun pausePlayer() {
//        presenter.pausePlayer()
////        mediaPlayer.pause()
//        handler.removeCallbacks(setProgressText)
//        updatePlaybackControlButton()//обновить кнопку
////        playerState = STATE_PAUSED
////        Glide.with(play)
////            .load(R.drawable.play_track)
////            .into(play)
//    }

    override fun enablePlayButton() {
        play.isEnabled = true
    }

    override fun updatePlaybackControlButton() {
        val iconRes = if (presenter.getPlayerState() == STATE_PLAYING) {
            R.drawable.pause_track
        } else {
            R.drawable.play_track
        }
        Glide.with(play)
            .load(iconRes)
            .into(play)
    }

    override fun stopProgressUpdate() {
        handler.removeCallbacks(setProgressText)
    }

    private fun progressTextRenew() {
        timeRemained.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(presenter.getCurrentPosition())
//            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        handler.postDelayed(setProgressText, SET_PROGRESS_TEXT_DELAY)
    }

    companion object {
        const val KEY_BUNDLE = "KEY_BUNDLE"
        const val SET_PROGRESS_TEXT_DELAY = 500L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}