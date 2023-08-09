package com.example.playlistmaker.playlist.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistcreatorBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.playlist.presentation.PlaylistCreatorViewModel
import com.example.playlistmaker.playlist.presentation.models.PlaylistCreatorState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class PlaylistCreatorFragment : Fragment() {

    private val viewModel by viewModel<PlaylistCreatorViewModel>()

    private var _binding: FragmentPlaylistcreatorBinding? = null
    private val binding get() = _binding!!

    private var titleInputText = ""
    private var descriptionInputText = ""
    private var imageUri: Uri? = null
    private var imagePrivateStorageUri = ""

    private var titleTextWatcher: TextWatcher? = null
    private var descriptionTextWatcher: TextWatcher? = null

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistcreatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            titleInputText = savedInstanceState.getString(TITLE_TEXT, "")
            descriptionInputText = savedInstanceState.getString(DESCRIPTION_TEXT, "")
            val uriStr = savedInstanceState.getString(IMAGE_COVER, "")
            if (uriStr != "") {
                imageUri = Uri.parse(uriStr)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { screenState ->
            render(screenState)
        }

        controlCreateButton()

        binding.playlistCover.setImageURI(imageUri)

        setImagePicker()

        setTitleTextWatcher()

        setDescriptionTextWatcher()

        setCreateButton()

        setBackNavigation()

    }

    private fun render(screenState: PlaylistCreatorState) {
        when (screenState) {
            PlaylistCreatorState.Saving -> {
                val message =
                    String.format(getString(R.string.playlist_string_piece), titleInputText)
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }

            PlaylistCreatorState.Success -> {
                navigateOut()
            }

            PlaylistCreatorState.Error -> {
                Toast.makeText(requireContext(), getString(R.string.retry), Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    private fun setBackNavigation() {

        confirmDialog = buildDialog()

        binding.backNavBar.setOnClickListener {
            if (titleInputText.isEmpty() && descriptionInputText.isEmpty() && imageUri == null) {
                navigateOut()
            } else confirmDialog.show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (titleInputText.isEmpty() && descriptionInputText.isEmpty() && imageUri == null) {
                    navigateOut()
                } else confirmDialog.show()
            }
        })
    }

    private fun setCreateButton() {
        binding.createButton.setOnClickListener {
            if (titleInputText.isNotEmpty()) {
                imageUri?.let { saveImageToPrivateStorage(it) }
                viewModel.savePlaylist(titleInputText, descriptionInputText, imagePrivateStorageUri)
            }
        }
    }

    private fun setImagePicker() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.playlistCover.setImageURI(uri)
                    imageUri = uri
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.empty_image_selection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setDescriptionTextWatcher() {
        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                descriptionInputText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                setTextInputViewColors(binding.playlistDescriptionField, descriptionInputText)
            }
        }
        descriptionTextWatcher?.let { binding.playlistDescription.addTextChangedListener(it) }
    }

    private fun setTitleTextWatcher() {
        titleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                titleInputText = s.toString()
                controlCreateButton()
            }

            override fun afterTextChanged(s: Editable?) {
                setTextInputViewColors(binding.playlistTitleField, titleInputText)
            }
        }
        titleTextWatcher?.let { binding.playlistTitle.addTextChangedListener(it) }
    }


    private fun buildDialog() =
        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.finish_playlist_creation))
            .setMessage(getString(R.string.data_loss_message))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
            }.setPositiveButton(getString(R.string.finish)) { dialog, which ->
                navigateOut()
            }

    private fun saveImageToPrivateStorage(uri: Uri) {

        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            getString(R.string.playlists_folder_name)
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(
            filePath,
            getString(R.string.playlist_cover_piece) + UUID.randomUUID()
                .toString() + getString(R.string.jpg)
        )

        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        imagePrivateStorageUri = file.absolutePath
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TITLE_TEXT, titleInputText)
        outState.putString(DESCRIPTION_TEXT, descriptionInputText)
        outState.putString(IMAGE_COVER, imageUri?.let { imageUri.toString() })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        titleTextWatcher?.let { binding.playlistTitle.removeTextChangedListener(it) }
        descriptionTextWatcher?.let { binding.playlistDescription.removeTextChangedListener(it) }
        _binding = null
    }

    private fun controlCreateButton() {
        binding.createButton.isEnabled = titleInputText.isNotEmpty()
    }

    private fun navigateOut() {
        val trackId = arguments?.getString(TRACK_ID)
        if (trackId.isNullOrEmpty()) {
            findNavController().navigateUp()
        } else {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(TRACK_ID, trackId)
            startActivity(playerIntent)
            activity?.finish()
        }
    }

    private fun setTextInputViewColors(textInputField: TextInputLayout, text: String) {
        val strokeColorRes = if (text.isEmpty()) {
            R.color.selector_box_stroke_color_initial
        } else {
            R.color.selector_box_stroke_color
        }
        val hintTextColorRes = if (text.isEmpty()) {
            R.color.selector_hint_text_color
        } else {
            strokeColorRes
        }
        val colorStateList =
            ResourcesCompat.getColorStateList(resources, strokeColorRes, requireContext().theme)
        val colorHintList =
            ResourcesCompat.getColorStateList(resources, hintTextColorRes, requireContext().theme)


        if (colorStateList != null) {
            textInputField.setBoxStrokeColorStateList(colorStateList)
            textInputField.hintTextColor = colorStateList
        }
        if (colorStateList != null) textInputField.defaultHintTextColor = colorHintList
    }


    companion object {

        const val TRACK_ID = "TRACK_ID"
        private const val TITLE_TEXT = "TITLE_TEXT"
        private const val DESCRIPTION_TEXT = "DESCRIPTION_TEXT"
        private const val IMAGE_COVER = "IMAGE_COVER"

    }
}