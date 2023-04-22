package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.ThemeManager
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.player.ui.TrackPlayer
import com.example.playlistmaker.player.ui.TrackPlayerImpl
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
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

    single {
        ThemeManager(get())
    }

}