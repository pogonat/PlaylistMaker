package com.example.playlistmaker.playlist.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistcreatorBinding
import com.example.playlistmaker.playlist.presentation.PlaylistCreatorViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreatorFragment : Fragment() {

    private val viewModel by viewModel<PlaylistCreatorViewModel>()

    private var _binding: FragmentPlaylistcreatorBinding? = null
    private val binding get() = _binding!!

    private var titleInputText = ""
    private var descriptionInputText = ""
    private var imageUri: Uri? = null

    private var titleTextWatcher: TextWatcher? = null
    private var descriptionTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        binding.playlistCover.setImageURI(imageUri)

        binding.backNavBar.setOnClickListener {
            findNavController().navigateUp()
        }

        if (titleInputText.isNullOrEmpty()) {
            binding.createButton.isEnabled = false
        } else {
            binding.createButton.isEnabled = true
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.playlistCover.setImageURI(uri)
                    imageUri = uri
//                    saveImageToPrivateStorage(uri)
                } else {
                    Log.d("PlaylistMaker", "No media selected")
                }
            }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        titleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val color = if (s.isNullOrEmpty() && !binding.playlistTitleField.isFocused) {
                    ContextCompat.getColor(requireContext(), R.color.colorSecondaryVariant)
                } else {
                    ContextCompat.getColor(requireContext(), R.color.yp_blue)
                }
                binding.playlistTitleField.boxStrokeColor = color
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        titleTextWatcher?.let { binding.playlistTitle.addTextChangedListener(it) }

        binding.playlistTitleField.setOnFocusChangeListener{_, hasFocus, ->
            if(!hasFocus && !titleInputText.isNullOrEmpty()) {
                binding.playlistTitle.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.yp_blue)
            }
        }

        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    descriptionInputText = s.toString()
                } else {
                    descriptionInputText = ""
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        descriptionTextWatcher?.let { binding.playlistDescription.addTextChangedListener(it) }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TITLE_TEXT, titleInputText)
        outState.putString(DESCRIPTION_TEXT, descriptionInputText)
        outState.putString(IMAGE_COVER, imageUri.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val TITLE_TEXT = "TITLE_TEXT"
        private const val DESCRIPTION_TEXT = "DESCRIPTION_TEXT"
        private const val IMAGE_COVER = "IMAGE_COVER"

    }
}