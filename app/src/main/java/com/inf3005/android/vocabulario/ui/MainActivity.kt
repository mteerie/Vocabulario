package com.inf3005.android.vocabulario.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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

//        Für die Theme-Variante des Splash-Screens vorübergehend eingebaut.
//        setTheme(R.style.Theme_Vocabulario)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        /**
         * Da die App ein Theme verwendet, das zunächst keine über ActionBar verfügt, muss im
         * Layout der Main Activity eine ActionBar erzeugt werden. Hier wird festgelegt,
         * dass die App die eigens erstellte Toolbar als solche ansieht.
         * */
        setSupportActionBar(binding.toolbar)

        /**
         * Das drawerLayout muss als lateinit variable erzeugt und hier initialisiert werden,
         * da es für die Funktion setDrawerState benötigt wird.
         * */
        drawerLayout = binding.drawerLayout

        /**
         * Dieser Code-Block setzt das navHostFragment und den navigationController fest.
         * */
        navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id)
                as NavHostFragment

        navigationController = navHostFragment.navController

        /**
         * Die navigationView (Navigation Drawer) wird mit dem Navigation Controller verknüpft,
         * um über den Navigation Graph die Ziele der App ansteuern zu können.
         *
         * Die Action Bar wird ebenfalls mit dem Navigation Controller verknüpft, um bspw. auch im
         * Add-Edit-Fragment einen Up-Button in der Action Bar anzeigen zu können.
         * */
        binding.navigationView.setupWithNavController(navigationController)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.listFragment, R.id.infoFragment, R.id.binFragment),
            drawerLayout
        )
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

