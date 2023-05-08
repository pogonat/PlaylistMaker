package com.example.playlistmaker.settings.presentation.models

sealed class SettingsSwitcherState {
    object On: SettingsSwitcherState()
    object Off: SettingsSwitcherState()
}