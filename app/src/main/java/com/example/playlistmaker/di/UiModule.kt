package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.ThemeManager
import com.example.playlistmaker.mediaactivity.presentation.FavoritesViewModel
import com.example.playlistmaker.mediaactivity.ui.PlaylistsViewModel
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.presentation.TrackPlayer
import com.example.playlistmaker.player.presentation.TrackPlayerImpl
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {

    factory<TrackPlayer> {
        TrackPlayerImpl(mediaPlayer = get())
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    viewModel<PlayerViewModel> {
        PlayerViewModel(playerInteractor = get(), trackPlayer = get())
    }

    viewModel<SearchViewModel> {
        SearchViewModel(searchInteractor = get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(settingsInteractor = get(), sharingInteractor = get())
    }

    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel()
    }

    viewModel<FavoritesViewModel> {
        FavoritesViewModel()
    }

    single {
        ThemeManager(get())
    }

}