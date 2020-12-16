package com.inf3005.android.vocabulario.utilities

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.inf3005.android.vocabulario.R

/**
 * Dieses Fragment dient als Startbildschirm der Anwendung und ist zugleich
 * der Startpunkt für die Vokabelabfrage.
 */
class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /**
         * Das Fragment zeigt ein "Menü" an - hier ist nur ein Link zum InfoFragment
         * implementiert, weshalb statt des Menüs lediglich ein Button in der ActionBar
         * angezeigt wird.
         * */
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_start, container, false)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_bar_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.option_delete_all).isVisible = false
    }
}