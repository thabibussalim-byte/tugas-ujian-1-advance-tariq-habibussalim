package com.example.blusq.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.blusq.data.local.entity.EventEntity
import com.example.blusq.data.local.room.EventDao
import com.example.blusq.databinding.FragmentSettingBinding
import com.example.blusq.di.Injection
import com.example.blusq.ui.main.MainViewModel
import com.example.blusq.ui.SettingPreferences
import com.example.blusq.ui.ViewModelFactory
import com.example.blusq.ui.dataStore

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = SettingPreferences.Companion.getInstance(requireContext().dataStore)
        val eventRepository = Injection.provideRepository(requireContext())


        val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref, eventRepository))[MainViewModel::class.java]

        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
                mainViewModel.saveThemeSetting(isDarkModeActive = true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
                mainViewModel.saveThemeSetting(isDarkModeActive = false)
            }
        }


        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.saveThemeSetting(isChecked)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}