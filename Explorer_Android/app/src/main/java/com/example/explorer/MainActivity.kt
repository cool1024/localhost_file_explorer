package com.example.explorer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.explorer.databinding.ActivityMainBinding
import com.example.explorer.http.HttpConfig
import com.example.explorer.http.HttpRequest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navCtrl: NavController
    private var settingsMenu: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val host = preference.getString("host", "http://localhost")!!

        HttpRequest.prepare(
            HttpConfig(
                requestHost = host,
                requestTimeout = 1000
            )
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        initNavigation()
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        settingsMenu = menu?.getItem(0)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            navCtrl.navigate(R.id.settingsFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initNavigation() {
        setSupportActionBar(binding.appToolbarLayout.appToolbar)
        navCtrl = (binding.navHostFragment.getFragment<NavHostFragment>()).navController
        val appBarConfig = AppBarConfiguration(navCtrl.graph)
        binding.appToolbarLayout.appToolbar.setupWithNavController(navCtrl, appBarConfig)
        navCtrl.addOnDestinationChangedListener { _, destination, _ ->
            settingsMenu?.isVisible = destination.id != R.id.settingsFragment
        }
    }
}
