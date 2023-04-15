package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.search.ui.SearchViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : ComponentActivity() {

    private val viewModel by viewModels<SearchViewModel> { SettingsViewModel.getViewModelFactory() }
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        binding.themeSwitcher.isChecked = (applicationContext as App).darkTheme

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            if (sharedPrefs.getString(THEME_SWITCHER, "") == DARK_THEME_SWITCHER_ON) {
                sharedPrefs.edit().putString(THEME_SWITCHER, DARK_THEME_SWITCHER_OFF).apply()
            } else {
                sharedPrefs.edit().putString(THEME_SWITCHER, DARK_THEME_SWITCHER_ON).apply()
            }
        }

        binding.shareButton.setOnClickListener {
            val link = getString(R.string.share_link)
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, link)
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.supportButton.setOnClickListener {
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

        binding.agreementButton.setOnClickListener {
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
    }
}