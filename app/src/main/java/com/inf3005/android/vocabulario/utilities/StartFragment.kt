package com.inf3005.android.vocabulario.utilities

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.inf3005.android.vocabulario.R
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

        /**
         * Das Fragment zeigt ein "Menü" an - hier ist nur ein Link zum InfoFragment
         * implementiert, weshalb statt des Menüs lediglich ein Button in der ActionBar
         * angezeigt wird.
         * */
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_start, container, false)

    }


    /**
     * Hier wird über den MenuInflater das Layout dot_menu für das Menü in der ActionBar inflated.
     * */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.dot_menu,menu)
    }

    /**
     * Mit Hilfe der Navigation Components (NavController, Navigation Graph) kann hier
     * problemlos ohne weitere programmatische Implementierung zum InfoFragment navigiert werden,
     * weil die Fragment-ID im dot_menu-Layout und im Navigation Graph identisch sind.
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    /**
    * Zeige im StartFragment nicht die Option an alle Einträge zu löschen.
    */
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.option_delete_all).isVisible = false
    }
}