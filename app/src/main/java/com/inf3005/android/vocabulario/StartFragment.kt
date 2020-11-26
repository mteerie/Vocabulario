package com.inf3005.android.vocabulario

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import timber.log.Timber

/**
 * Dieses Fragment dient als Startbildschirm der Anwendung und ist zugleich
 * der Startpunkt für die Vokabelabfrage.
 */
class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_start, container, false)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.dot_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    /**
     * Zwecks Debugging implementierte Funktion
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("onDestroyView called.")
    }
}