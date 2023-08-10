package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.core.ThemeManager
import com.example.playlistmaker.media.presentation.FavouritesViewModel
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.presentation.TrackPlayer
import com.example.playlistmaker.player.presentation.TrackPlayerImpl
import com.example.playlistmaker.playlist.presentation.PlaylistContentsViewModel
import com.example.playlistmaker.playlist.presentation.PlaylistCreatorViewModel
import com.example.playlistmaker.presentation.models.TrackToTrackUIModelConverter
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
        PlayerViewModel(
            playerInteractor = get(),
            trackPlayer = get(),
            favouritesInteractor = get(),
            playlistInteractor = get(),
            trackToTrackUIModelConverter = get()
        )
    }

    viewModel<SearchViewModel> {
        SearchViewModel(searchInteractor = get(), trackToTrackUIModelConverter = get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(settingsInteractor = get(), sharingInteractor = get())
    }

    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel(playlistInteractor = get())
    }

    viewModel<FavouritesViewModel> {
        FavouritesViewModel(favInteractor = get(), trackToTrackUIModelConverter = get())
    }

    viewModel<PlaylistCreatorViewModel> {
        PlaylistCreatorViewModel(playlistInteractor = get())
    }

    viewModel<PlaylistContentsViewModel>() {
        PlaylistContentsViewModel(playlistInteractor = get(), trackUIModelConverter = get(), sharingInteractor = get())
    }

    single<ThemeManager> {
        ThemeManager(get())
    }

    single<TrackToTrackUIModelConverter> {
        TrackToTrackUIModelConverter()
    }

}