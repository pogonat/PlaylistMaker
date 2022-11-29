package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val share = findViewById<FrameLayout>(R.id.shareButton)
        val support = findViewById<FrameLayout>(R.id.supportButton)
        val readAgreement = findViewById<FrameLayout>(R.id.agreementButton)

        share.setOnClickListener {
            val link = "https://practicum.yandex.ru/profile/android-developer/"
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.data = Uri.parse(link)
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        support.setOnClickListener {
            val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
            val email = "pogorelskayanat@yandex.ru"
            val emailSubject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.data = Uri.parse("mailto:")
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
            sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            val supportIntent = Intent.createChooser(sendIntent, null)
            startActivity(supportIntent)
        }

        readAgreement.setOnClickListener {
            val link = "https://yandex.ru/legal/practicum_offer/"
            val readIntent = Intent(Intent.ACTION_SEND)
            readIntent.data = Uri.parse(link)
            val readAgreementIntent = Intent.createChooser(readIntent, null)
            startActivity(readAgreementIntent)
        }
    }
}