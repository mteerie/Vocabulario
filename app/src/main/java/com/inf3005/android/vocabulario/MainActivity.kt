package com.inf3005.android.vocabulario

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navigationController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val bottomNavigation : BottomNavigationView = findViewById(R.id.bottom_nav_view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment

        navigationController = navHostFragment.navController

        setupActionBarWithNavController(navHostFragment.navController)

//        bottomNavigation.setupWithNavController(navHostFragment.navController)
    }

    override fun onSupportNavigateUp(): Boolean {

        hideKeyboard()

        return navigationController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Zum Aufruf in onSupportNavigateUp. Die Tastatur soll versteckt werden, wenn der Nutzer eines
     * der Eingabefelder im Add-Edit-Fragment anklickt und mit geöffneter Tastatur den Back-Button
     * in der ActionBar betätigt.
     *
     * Normales Verhalten: Die Tastatur bleibt geöffnet und die Eingabe verschwindet ins Nirvana?
     * */
    private fun hideKeyboard() {
        val view = this.currentFocus

        if (view != null) {
            val manager = getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager

            manager.hideSoftInputFromWindow(
                view.windowToken, 0
            )
        }
    }
}

