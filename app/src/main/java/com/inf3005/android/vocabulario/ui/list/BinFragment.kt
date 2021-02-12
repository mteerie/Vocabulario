package com.inf3005.android.vocabulario.ui.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyAdapter
import com.inf3005.android.vocabulario.databinding.FragmentBinBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class BinFragment : Fragment(R.layout.fragment_bin), VocabularyAdapter.EntryClickListener {

    private lateinit var searchActionView: SearchView

    private lateinit var fab: FloatingActionButton

    private val viewModel: BinViewModel by viewModels()

    /**
     * Beim Anklicken eines Eintrags, wird er wieder in die Vokabelliste verschoben.
     *
     * Snackbar wird erzeugt und erlaubt Verschieben rückgängig zu machen.
     */
    override fun onCardClick(entry: Vocabulary) {
        viewModel.updateBinnedState(entry, state = false)

        Snackbar.make(
            requireView(),
            getString(R.string.restored_from_bin),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.list_entry_undo)) {
                viewModel.updateBinnedState(entry, state = true)
            }
            .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            .setAnchorView(fab)
            .show()
        return
    }

    /**
     * Wegen Erweiterung des EntryClickListener muss die Funktion überschrieben werden.
     *
     * Wird in BinFragent aber nicht verwendet.
     * */
    override fun onTextToSpeechIconClick(entry: Vocabulary) {
        return
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val binding = FragmentBinBinding.bind(view)

        val vocabularyAdapter = VocabularyAdapter(this)

        fab = binding.fab

        binding.apply {
            list.apply {
                adapter = vocabularyAdapter
                setHasFixedSize(true)
            }

            /**
             * Observer entscheidet über Sichtbarkeit des emptyBinText und über Status des FAB.
             *
             * Dialog zum Löschen aller Einträge im Papierkorb soll nur aufrufbar sein, wenn mind.
             * ein Eintrag vorhanden ist.
             * */
            viewModel.binnedEntryCount.observe(viewLifecycleOwner) { entry ->
                binding.emptyBinText.isVisible = entry == 0
                fab.isEnabled = entry > 0
            }

            // Erzeugt Dialog zur Abfrage ob alle Einträge im Papierkorb gelöscht werden sollen.
            fab.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.delete_all_entries_title))
                    .setMessage(getString(R.string.delete_all_entries))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.delete_all_entries_positive)) { _, _ ->
                        viewModel.deleteBinnedEntries()
                        Toast.makeText(
                            context, getString(R.string.emptied_bin), Toast.LENGTH_LONG
                        )
                            .show()
                    }
                    .setNegativeButton(
                        getString(R.string.delete_all_entries_negative), null
                    )
                    .show()
            }

            // Analog zu ListFragment für Einträge mit binned == true.
            viewModel.binnedEntries.observe(viewLifecycleOwner)
            {
                vocabularyAdapter.submitList(it)
            }

            setHasOptionsMenu(true)
        }
    }

    // Über Anzeige des Such-Icons entscheiden. Nur sichtbar, wenn mind. ein Eintrag binned ist.
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val searchOption = menu.findItem(R.id.option_search)

        viewModel.binnedEntryCount.observe(viewLifecycleOwner) { entry ->
            searchOption.isVisible = entry > 0
        }
    }


    // Suchfunktionalität in Action Bar implementieren - analog zu ListFramgent.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.bin_action_bar_menu, menu)

        val searchOption = menu.findItem(R.id.option_search)

        searchActionView = searchOption.actionView as SearchView

        val stateQuery = viewModel.getBinSearchQuery()

        if (!stateQuery.isNullOrEmpty()) {
            searchOption.expandActionView()
            searchActionView.setQuery(stateQuery, false)
        }

        searchActionView
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.setBinSearchQuery(newText)
                    return true
                }
            })
    }
}