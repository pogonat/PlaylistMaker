package com.example.playlistmaker.sharing.presentation

import android.content.Intent

interface SharingInteractor {
    fun shareApp(): Intent
    fun shareApp(message: String): Intent
    fun openTerms(): Intent
    fun openSupport(): Intent
}