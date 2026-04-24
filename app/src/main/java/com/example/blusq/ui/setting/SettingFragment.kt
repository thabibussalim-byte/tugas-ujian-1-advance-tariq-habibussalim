package com.example.blusq.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.blusq.R
import com.example.blusq.databinding.FragmentSettingBinding
import com.example.blusq.di.Injection
import com.example.blusq.ui.main.MainViewModel
import com.example.blusq.ui.SettingPreferences
import com.example.blusq.ui.ViewModelFactory
import com.example.blusq.ui.dataStore
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val eventRepository = Injection.provideRepository(requireContext())
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref, eventRepository))[MainViewModel::class.java]

        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
                binding.tvName.text = getString(R.string.tv_dark_mode_active)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
                binding.tvName.text = getString(R.string.tv_dark_mode_inactive)
            }
        }

        mainViewModel.getNotifSettings().observe(viewLifecycleOwner) { isNotifActive: Boolean ->
            binding.switchNotif.isChecked = isNotifActive
            if (isNotifActive) {
                binding.tvName1.text = getString(R.string.tv_notif_active)
            } else {
                binding.tvName1.text = getString(R.string.tv_notif_inactive)
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.saveThemeSetting(isChecked)
        }

        binding.switchNotif.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.saveNotifSetting(isChecked)
            if (isChecked) {
                startDailyReminder()
            } else {
                cancelDailyReminder()
            }
        }


    }

    private fun startDailyReminder() {
        val workManager = WorkManager.getInstance(requireContext())
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun cancelDailyReminder() {
        val workManager = WorkManager.getInstance(requireContext())
        workManager.cancelUniqueWork(WORK_NAME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val WORK_NAME = "daily_reminder_work"
    }
}