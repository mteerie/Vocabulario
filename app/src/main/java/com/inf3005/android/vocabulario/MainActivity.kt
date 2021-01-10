package com.inf3005.android.vocabulario

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.inf3005.android.vocabulario.utilities.KeyboardUtilities
import com.inf3005.android.vocabulario.utilities.NavigationDrawerState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationDrawerState {
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var navigationController: NavController

    private lateinit var navigationView: NavigationView

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var toolbar: MaterialToolbar

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment

        navigationController = navHostFragment.navController

        navigationView = findViewById(R.id.navigation_view)

        navigationView.setupWithNavController(navigationController)

        drawerLayout = findViewById(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration(navigationController.graph, drawerLayout)

        setupActionBarWithNavController(navigationController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        KeyboardUtilities.hideKeyboard(this)
        return navigationController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        /**
         * Zum Aufruf in onSupportNavigateUp. Die Tastatur soll versteckt werden, wenn der Nutzer eines
         * der Eingabefelder im Add-Edit-Fragment anklickt und mit geöffneter Tastatur den Back-Button
         * in der ActionBar betätigt.
         * */
        KeyboardUtilities.hideKeyboard(this)
    }

    /**
     * Die Funktion soll von unterschiedlichen Zielen innerhalb der App (Fragments oder ggf.
     * anderen Activities) mit Übergabe eines Boolean aufgerufen werden können.
     *
     * Soll der Navigation Drawer nicht über Gesten aufrufbar sein (bspw. im AddEditFragment),
     * so wird false übergeben.
     * */
    override fun setDrawerState(enabled: Boolean) {
        drawerLayout.setDrawerLockMode(
            if (enabled)
                DrawerLayout.LOCK_MODE_UNLOCKED
            else
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )
    }
}

