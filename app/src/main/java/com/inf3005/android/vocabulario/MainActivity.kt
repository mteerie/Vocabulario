package com.inf3005.android.vocabulario

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.getString
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Initialisiere die Timber-Library zwecks Debugging.
         */
        Timber.plant(Timber.DebugTree())
        Timber.i("onCreate called.")

        /**
         * Erzeuge ein Value für die BottomNavigationView im Layout-File der Main Activity.
         */
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        /**
         * Instanziiere den navController über das nav_host_fragment und verknüpfe ihn mit
         * bottomNavigation
         */
        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavigation.setupWithNavController(navController)
    }
}