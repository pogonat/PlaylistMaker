package com.example.playlistmaker.playlist.ui
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.playlist.presentation.PlaylistEditorViewModel
import com.example.playlistmaker.playlist.presentation.models.PlaylistEditorState

class PlaylistEditorFragment : PlaylistCreatorFragment() {
    override val viewModel by viewModel<PlaylistEditorViewModel>()

    private lateinit var playlist: Playlist

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = getIdFromArgs()

        setBackNavigation()

        viewModel.editorState.observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistEditorState.Content -> renderCurrentPlaylistInfo(it.playlist)
            }
        }

        viewModel.getPlaylistById(playlistId)

    }

    private fun renderCurrentPlaylistInfo(foundPlaylist: Playlist) {
        playlist = foundPlaylist
        titleInputText = foundPlaylist.playlistName
        descriptionInputText = foundPlaylist.playlistDescription
        imageUri = Uri.parse(foundPlaylist.imagePath)
        imagePrivateStorageUri = foundPlaylist.imagePath
        binding.apply {
            backNavBar.text = getString(R.string.edit)
            createButton.text = getString(R.string.save)
            playlistTitle.setText(titleInputText)
            playlistDescription.setText(descriptionInputText)

            Glide.with(playlistCover)
                .load(imageUri)
                .centerCrop()
                .transform(RoundedCorners(15))
                .placeholder(R.drawable.placeholder_image)
                .into(playlistCover)
        }
    }

    override fun setBackNavigation() {
        binding.backNavBar.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun getIdFromArgs(): Int {
        return requireArguments().getInt(ARGS_PLAYLIST_ID)
    }

    companion object {

        private const val ARGS_PLAYLIST_ID = "ARGS_PLAYLIST_ID"
        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
}