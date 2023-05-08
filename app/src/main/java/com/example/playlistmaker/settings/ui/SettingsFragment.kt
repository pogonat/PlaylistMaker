package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.models.SettingsSwitcherState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private  lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTheme()

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.updateThemeSettings(checked)
        }

        binding.shareButton.setOnClickListener {
            viewModel.share()
        }

        binding.supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        binding.agreementButton.setOnClickListener {
            viewModel.openTerms()
        }

//        binding.arrowReturn.setOnClickListener{
//            finish()
//        }

        viewModel.getSwitcherStateLiveData().observe(viewLifecycleOwner) { switcherState ->
            render(switcherState)
        }

        viewModel.navigationEvent.observe(viewLifecycleOwner) { intent ->
            startActivity(intent)
        }
    }

    private fun render(switcherState: SettingsSwitcherState) {
        when (switcherState) {
            SettingsSwitcherState.On -> {
                binding.themeSwitcher.isChecked = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            SettingsSwitcherState.Off -> {
                binding.themeSwitcher.isChecked = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

}