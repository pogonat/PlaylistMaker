package com.example.playlistmaker.sharing.presentation

import com.example.playlistmaker.sharing.domain.models.EmailData
import com.example.playlistmaker.sharing.presentation.models.IntentTransfer

interface ExternalNavigator {
    fun shareLink(url: String)
    fun openLink(url: String)
    fun openEmail(emailData: EmailData)
}