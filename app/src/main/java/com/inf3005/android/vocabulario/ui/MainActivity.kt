package com.inf3005.android.vocabulario.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.databinding.ActivityMainBinding
import com.inf3005.android.vocabulario.utilities.KeyboardUtilities
import com.inf3005.android.vocabulario.utilities.NavigationDrawerState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationDrawerState {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navHostFragment: NavHostFragment

    private lateinit var navigationController: NavController

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {


        // Setze das korrekte Theme bei Erzeugung der Activity und löse den Splash-Screen ab.
        setTheme(R.style.Theme_Vocabulario)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        /**
         * Da die App ein Theme verwendet, das zunächst über keine ActionBar verfügt, muss im
         * Layout der Main Activity eine ActionBar erzeugt werden. Hier wird festgelegt,
         * dass die App die eigens erstellte Toolbar als solche ansieht.
         * */
        setSupportActionBar(binding.toolbar)

        /**
         * Das drawerLayout muss als lateinit variable erzeugt und hier initialisiert werden,
         * da es für die Funktion setDrawerState benötigt wird.
         * */
        drawerLayout = binding.drawerLayout


        // Lege das navHostFragment und den navigationController fest.
        navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id)
                as NavHostFragment

        navigationController = navHostFragment.findNavController()

        /**
         * Die navigationView wird mit dem Navigation Controller verknüpft, um über den Navigation
         * Graph die Ziele der App ansteuern zu können.
         *
         * Die Action Bar wird ebenfalls mit dem Navigation Controller verknüpft, um bspw. auch im
         * Add-Edit-Fragment einen Up-Button in der Action Bar anzeigen zu können.
         * */
        binding.navigationView.setupWithNavController(navigationController)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.listFragment, R.id.binFragment, R.id.infoFragment),
            drawerLayout
        )
        setupActionBarWithNavController(navigationController, appBarConfiguration)
    }

    /**
     * Überschreiben um navigationController die Navigation bei Betätigen des Up-Button zu
     * überlassen.
     * */
    override fun onSupportNavigateUp(): Boolean {
        return navigationController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Regelt ob der Navigation Drawer per Gesten vom Nutzer aufgerufen werden kann.
     *
     * Zur Verwendung in AddEditFragment.
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

