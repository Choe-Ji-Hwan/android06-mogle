package com.wakeup.presentation.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.wakeup.presentation.R
import com.wakeup.presentation.databinding.ActivityMainBinding
import com.wakeup.presentation.model.WeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNav()
        setSpinner()

        viewModel.testGetWeather()
    }

    fun openNavDrawer() {
        binding.drawerLayout.open()
    }

    private fun setBottomNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        setTopLevelDestinations(navController)

        binding.fab.setOnClickListener {
            navController.navigate(R.id.add_moment_navigation)
        }
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter(
            this, R.layout.item_menu, listOf(
                WeatherTheme.AUTO.str,
                WeatherTheme.BRIGHT.str,
                WeatherTheme.DARK.str,
                WeatherTheme.CLOUDY.str
            )
        )
        binding.layoutDrawer.spinnerTheme.adapter = adapter
    }

    private fun setTopLevelDestinations(navController: NavController) {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.map_fragment,
                R.id.globe_fragment
            )
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopDest = appBarConfiguration.topLevelDestinations.contains(destination.id)
            binding.bottomAppBar.isVisible = isTopDest
            binding.fab.isVisible = isTopDest
        }
    }
}