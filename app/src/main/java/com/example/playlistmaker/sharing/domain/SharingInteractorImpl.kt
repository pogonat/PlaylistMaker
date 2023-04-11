package com.example.playlistmaker.sharing.domain

import com.example.playlistmaker.sharing.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    fun getShareAppLink(): String {
        TODO()
    }

    fun getTermsLink(): String {
        TODO()
    }

    fun getSupportEmailData(): String {
        TODO()
    }

}