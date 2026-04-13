package com.example.blusq.ui.main

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.blusq.R
import com.example.blusq.data.local.room.EventDao
import com.example.blusq.databinding.ActivityMainBinding
import com.example.blusq.di.Injection
import com.example.blusq.ui.SettingPreferences
import com.example.blusq.ui.ViewModelFactory
import com.example.blusq.ui.dataStore


class MainActivity (): AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        setupNavController()
        setupToolbar()
        setupNavigationUI()
        setupAppBarConfiguration()
        setupTheme()



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
}

