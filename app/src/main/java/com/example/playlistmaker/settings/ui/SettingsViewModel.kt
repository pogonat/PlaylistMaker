package com.example.playlistmaker.settings.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.SharingInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_settings)

            val sharedPrefs = getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
            val share = findViewById<FrameLayout>(R.id.shareButton)
            val support = findViewById<FrameLayout>(R.id.supportButton)
            val readAgreement = findViewById<FrameLayout>(R.id.agreementButton)
            val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

            themeSwitcher.isChecked = (applicationContext as App).darkTheme

            themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
                (applicationContext as App).switchTheme(checked)
                if (sharedPrefs.getString(THEME_SWITCHER, "") == DARK_THEME_SWITCHER_ON) {
                    sharedPrefs.edit().putString(THEME_SWITCHER, DARK_THEME_SWITCHER_OFF).apply()
                } else {
                    sharedPrefs.edit().putString(THEME_SWITCHER, DARK_THEME_SWITCHER_ON).apply()
                }
            }

            share.setOnClickListener {
                val link = getString(R.string.share_link)
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.type = "text/plain"
                sendIntent.putExtra(Intent.EXTRA_TEXT, link)
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

            support.setOnClickListener {
                val message = getString(R.string.support_message)
                val email = getString(R.string.support_email)
                val emailSubject = getString(R.string.support_subject)
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.data = Uri.parse("mailto:")
                sendIntent.putExtra(Intent.EXTRA_TEXT, message)
                sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                val supportIntent = Intent.createChooser(sendIntent, null)
                startActivity(supportIntent)
            }

            readAgreement.setOnClickListener {
                val link = getString(R.string.agreement_link)
                val readIntent = Intent(Intent.ACTION_VIEW)
                readIntent.data = Uri.parse(link)
                val readAgreementIntent = Intent.createChooser(readIntent, null)
                startActivity(readAgreementIntent)
            }
        }

        companion object {
            const val THEME_SWITCHER = "THEME_SWITCHER"
            const val DARK_THEME_SWITCHER_ON = "on"
            const val DARK_THEME_SWITCHER_OFF = "off"

            fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
                initializer {
                    SettingsViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                }
            }
        }

}