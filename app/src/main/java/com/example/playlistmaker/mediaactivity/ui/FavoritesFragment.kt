package com.example.playlistmaker.mediaactivity.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.mediaactivity.ui.models.FavoritesState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoritesFragment: Fragment() {

    companion object {
        fun newInstance() = FavoritesFragment().apply
        }
    }

    private val viewModel: FavoritesViewModel by viewModel {}

    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            when(it) {
                is FavoritesState.Loading -> showErrorMessage()
                is FavoritesState.Content -> showErrorMessage()
                is FavoritesState.Error -> showErrorMessage()
            }
        }
    }

    private fun showErrorMessage() {
        binding.apply {
            errorMessage.isVisible = true
        }
    }

}