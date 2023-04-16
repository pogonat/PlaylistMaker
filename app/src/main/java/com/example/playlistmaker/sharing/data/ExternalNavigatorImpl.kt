package com.example.playlistmaker.sharing.presentation

import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.models.EmailData
import com.example.playlistmaker.sharing.presentation.models.IntentTransfer

class ExternalNavigatorImpl: ExternalNavigator {

    override fun shareLink(url: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, url)
        val shareIntent = Intent.createChooser(sendIntent, null)
//        return IntentTransfer(shareIntent)
    }

    override fun openLink(url: String) {
        val readIntent = Intent(Intent.ACTION_VIEW)
        readIntent.data = Uri.parse(url)
        val readAgreementIntent = Intent.createChooser(readIntent, null)
//        return IntentTransfer(readAgreementIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.data = Uri.parse("mailto:")
        sendIntent.putExtra(Intent.EXTRA_TEXT, emailData.emailMessage)
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.emailAddress))
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.emailSubject)
        val supportIntent = Intent.createChooser(sendIntent, null)
//        return IntentTransfer(supportIntent)
    }
}