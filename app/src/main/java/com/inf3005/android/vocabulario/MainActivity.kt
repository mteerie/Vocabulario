package com.inf3005.android.vocabulario

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Erzeuge ein Value für die BottomNavigationView im Layout-File der Main Activity.
         */
        bottomNavigation = findViewById(R.id.bottom_nav_view)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment

        /**
         * Instanziiere den NavController über das NavHostFragment und verknüpfe ihn mit
         * bottomNavigation.
         */

        bottomNavigation.setupWithNavController(navHostFragment.navController)
        }
}