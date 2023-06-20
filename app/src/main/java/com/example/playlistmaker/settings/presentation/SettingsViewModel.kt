package com.example.playlistmaker.settings.presentation

import com.example.playlistmaker.core.SingleLiveEvent
import android.content.Intent
import androidx.lifecycle.*
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.models.DarkThemeSwitcher
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.example.playlistmaker.settings.presentation.models.SettingsSwitcherState
import com.example.playlistmaker.sharing.presentation.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

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

}