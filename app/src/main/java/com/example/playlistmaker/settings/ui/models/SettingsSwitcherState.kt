package com.example.playlistmaker.settings.ui.models

sealed class SettingsSwitcherState {
    object On: SettingsSwitcherState()
    object Off: SettingsSwitcherState()
}