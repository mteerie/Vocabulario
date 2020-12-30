package com.inf3005.android.vocabulario

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val bottomNavigation : BottomNavigationView = findViewById(R.id.bottom_nav_view)

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//                as NavHostFragment

//        bottomNavigation.setupWithNavController(navHostFragment.navController)
    }
}