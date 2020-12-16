package com.inf3005.android.vocabulario

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.getString
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inf3005.android.vocabulario.utilities.VocabularyApplication
import com.inf3005.android.vocabulario.voclist.VocabularyViewModel
import com.inf3005.android.vocabulario.voclist.VocabularyViewModelFactory
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Erzeuge ein Value für die BottomNavigationView im Layout-File der Main Activity.
         */
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        /**
         * Instanziiere den navController über das nav_host_fragment und verknüpfe ihn mit
         * bottomNavigation.
         */
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigation.setupWithNavController(navController)
    }
}