package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAgreementBinding

class AgreementFragment : Fragment() {

    private lateinit var binding: FragmentAgreementBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = requireContext().getString(R.string.agreement_link)

        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl(url)

        binding.backNavBar.setOnClickListener{
            findNavController().navigateUp()
        }

    }

}