package com.example.playlistmaker.sharing.domain

import android.content.Intent
import com.example.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(text: String): Intent
    fun openLink(url: String): Intent
    fun openEmail(emailData: EmailData): Intent
}