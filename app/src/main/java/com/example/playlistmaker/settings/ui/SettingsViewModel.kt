package com.example.playlistmaker.settings.ui

import com.example.playlistmaker.SingleLiveEvent
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.models.DarkThemeSwitcher
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.example.playlistmaker.settings.ui.models.SettingsSwitcherState

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsInteractor =
        Creator.provideSettingsInteractor(getApplication<Application>())

    private val sharingInteractor =
        Creator.provideSharingInteractor(getApplication<Application>())

    private val switcherLiveData = MutableLiveData<SettingsSwitcherState>()
    fun getSwitcherStateLiveData(): LiveData<SettingsSwitcherState> = switcherLiveData

    val navigationEvent = SingleLiveEvent<Intent>()

    private fun renderState(state: SettingsSwitcherState) {
        switcherLiveData.postValue(state)
    }

    fun getTheme() {
        val themeSettings = settingsInteractor.getThemeSettings()
        when (themeSettings.darkThemeSwitcher) {
            DarkThemeSwitcher.DARK_THEME_SWITCHER_ON -> renderState(SettingsSwitcherState.On)
            DarkThemeSwitcher.DARK_THEME_SWITCHER_OFF -> renderState(SettingsSwitcherState.Off)
        }
    }

    fun share() {
        val intent = sharingInteractor.shareApp()
        navigationEvent.postValue(intent)
    }

    fun openSupport() {
        val intent = sharingInteractor.openSupport()
        navigationEvent.postValue(intent)
    }

    fun openTerms() {
        val intent = sharingInteractor.openTerms()
        navigationEvent.postValue(intent)
    }

    fun updateThemeSettings(checked: Boolean) {

        when (checked) {

            true -> {
                settingsInteractor.updateThemeSettings(ThemeSettings(DarkThemeSwitcher.DARK_THEME_SWITCHER_ON))
                renderState(SettingsSwitcherState.On)
            }

            false -> {
                settingsInteractor.updateThemeSettings(ThemeSettings(DarkThemeSwitcher.DARK_THEME_SWITCHER_OFF))
                renderState(SettingsSwitcherState.Off)
            }
        }

    }

    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

}