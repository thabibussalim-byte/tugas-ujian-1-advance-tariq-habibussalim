package com.example.blusq.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.blusq.R
import com.example.blusq.databinding.ActivityMainBinding
import com.example.blusq.di.Injection
import com.example.blusq.ui.SettingPreferences
import com.example.blusq.ui.ViewModelFactory
import com.example.blusq.ui.dataStore
import com.example.blusq.ui.search.SearchViewModel
import com.example.blusq.ui.setting.DailyReminderWorker
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var searchViewModel: SearchViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        setupNavController()
        setupToolbar()
        setupNavigationUI()
        setupAppBarConfiguration()
        setupTheme()
        setupDailyReminder()
        setupNotificationPermission()
        setupSearch()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            setupActionBarTitleAndSubtitle(destination)

            when (destination.id) {
                R.id.upcomingFragment -> {
                    binding.tvTitle.text = getString(R.string.title_upcoming)
                    binding.tvTitle.visibility = View.VISIBLE
                    binding.tvSubtitle.visibility = View.GONE
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.toolbar.visibility = View.VISIBLE
                    binding.searchView.visibility = View.VISIBLE
                }

                R.id.finishFragment -> {
                    binding.tvTitle.text = getString(R.string.title_finish)
                    binding.tvTitle.visibility = View.VISIBLE
                    binding.tvSubtitle.visibility = View.GONE
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.toolbar.visibility = View.VISIBLE
                    binding.searchView.visibility = View.VISIBLE

                }

                R.id.favoriteFragment -> {
                    binding.tvTitle.text = getString(R.string.title_favorite)
                    binding.tvTitle.visibility = View.VISIBLE
                    binding.tvSubtitle.text = getString(R.string.subtitle_favorite)
                    binding.tvSubtitle.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.toolbar.visibility = View.VISIBLE
                    binding.searchView.visibility = View.GONE
                }

                R.id.settingFragment -> {
                    binding.tvTitle.text = getString(R.string.title_setting)
                    binding.tvTitle.visibility = View.VISIBLE
                    binding.tvSubtitle.visibility = View.GONE
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.toolbar.visibility = View.VISIBLE
                    binding.searchView.visibility = View.GONE

                }
                R.id.detailEventFragment -> {
                    binding.tvTitle.visibility = View.GONE
                    binding.tvSubtitle.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                    binding.toolbar.visibility = View.GONE
                    binding.searchView.visibility = View.GONE
                }
                R.id.searchFragment -> {
                    binding.tvTitle.visibility = View.GONE
                    binding.tvSubtitle.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                    binding.toolbar.visibility = View.GONE
                    binding.searchView.visibility = View.GONE
                }
                else -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun setupSearch() {
        val pref = SettingPreferences.getInstance(dataStore)
        val eventRepository = Injection.provideRepository(this)
        val factory = ViewModelFactory(pref, eventRepository)
        searchViewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchViewModel.setQuery(query)
                    if (navController.currentDestination?.id != R.id.searchFragment) {
                        navController.navigate(R.id.searchFragment)
                    }
                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setupNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupDailyReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            DailyReminderWorker::class.java,
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .addTag(DAILY_REMINDER_TAG)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DAILY_REMINDER_TAG,
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }


    private fun setupTheme() {
        val pref = SettingPreferences.getInstance(dataStore)
        val eventRepository = Injection.provideRepository(this)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref, eventRepository))[MainViewModel::class.java]
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
    private fun setupViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun setupNavController(){
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
    }
    private fun setupToolbar(){
        setSupportActionBar(binding.toolbar)
    }

    private fun setupNavigationUI(){
        binding.bottomNav.setupWithNavController(navController)

    }


    private fun setupAppBarConfiguration(){
        appBarConfiguration = AppBarConfiguration.Builder(

            R.id.upcomingFragment,
            R.id.finishFragment,
            R.id.favoriteFragment,
            R.id.settingFragment
        ).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupActionBarTitleAndSubtitle(destination: NavDestination) {
        supportActionBar?.title = when (destination.id) {
            R.id.upcomingFragment -> getString(R.string.title_upcoming)
            R.id.finishFragment -> getString(R.string.title_finish)
            R.id.favoriteFragment -> getString(R.string.title_favorite)
            R.id.settingFragment -> getString(R.string.title_setting)
            else -> getString(R.string.app_name)
        }

        supportActionBar?.subtitle = when (destination.id) {
            R.id.favoriteFragment -> getString(R.string.subtitle_favorite)
            else -> null
        }
    }

    override fun onSupportNavigateUp(): Boolean{
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        private const val DAILY_REMINDER_TAG = "daily_reminder"
    }
}