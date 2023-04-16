package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.models.EmailData
import com.example.playlistmaker.sharing.domain.SharingRepository

class SharingRepositoryImpl(private val context: Context): SharingRepository {
    override fun getShareAppLink(): String {
        return context.getString(R.string.share_link)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.agreement_link)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.support_message),
            context.getString(R.string.support_email),
            context.getString(R.string.support_subject)
        )
    }
}