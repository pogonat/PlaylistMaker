package com.example.playlistmaker.sharing.data

import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl: ExternalNavigator {

    override fun shareLink(url: String): Intent {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, url)
        return Intent.createChooser(sendIntent, null)
    }

    override fun openLink(url: String): Intent {
        val readIntent = Intent(Intent.ACTION_VIEW)
        readIntent.data = Uri.parse(url)
        return Intent.createChooser(readIntent, null)
    }

    override fun openEmail(emailData: EmailData): Intent {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.data = Uri.parse("mailto:")
        sendIntent.putExtra(Intent.EXTRA_TEXT, emailData.emailMessage)
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.emailAddress))
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.emailSubject)
        return Intent.createChooser(sendIntent, null)
    }
}