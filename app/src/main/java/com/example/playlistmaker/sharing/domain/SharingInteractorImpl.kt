package com.example.playlistmaker.sharing.domain

import android.content.Intent
import com.example.playlistmaker.sharing.domain.models.EmailData
import com.example.playlistmaker.sharing.presentation.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val repository: SharingRepository
) : SharingInteractor {

    override fun shareApp(): Intent {
        return externalNavigator.shareLink(getShareAppLink())
    }

    override fun shareApp(message: String): Intent {
        return externalNavigator.shareLink(message)
    }

    override fun openTerms(): Intent {
        return externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport(): Intent {
        return externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return repository.getShareAppLink()
    }

    private fun getTermsLink(): String {
        return repository.getTermsLink()
    }

    private fun getSupportEmailData(): EmailData {
        return repository.getSupportEmailData()
    }

}