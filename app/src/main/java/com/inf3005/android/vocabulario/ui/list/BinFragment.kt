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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyAdapter
import com.inf3005.android.vocabulario.databinding.FragmentBinBinding
import com.inf3005.android.vocabulario.utilities.onQueryChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class BinFragment : Fragment(R.layout.fragment_bin), VocabularyAdapter.EntryClickListener {

    private lateinit var searchActionView: SearchView

    private val viewModel: BinViewModel by viewModels()

    override fun onClick(entry: Vocabulary) {
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val binding = FragmentBinBinding.bind(view)

        val vocabularyAdapter = VocabularyAdapter(this)

        binding.apply {
            list.apply {
                adapter = vocabularyAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewModel.binnedEntryCount.observe(viewLifecycleOwner) { entry ->
                binding.emptyBinText.isVisible = entry == 0
                binding.fab.isEnabled = entry != 0
            }

            binding.fab.setOnClickListener {
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

            viewModel.binnedEntries.observe(viewLifecycleOwner)
            {
                vocabularyAdapter.submitList(it)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val entry = vocabularyAdapter.getEntryAt(viewHolder.adapterPosition)
                    viewModel.updateBinnedState(entry, state = false)

                    Snackbar.make(
                        requireView(),
                        getString(R.string.restored_from_bin),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.list_entry_undo)) {
                            viewModel.updateBinnedState(entry, state = true)
                        }
                        .setActionTextColor(ContextCompat.getColor(context!!, R.color.black))
                        .setTextColor(ContextCompat.getColor(context!!, R.color.black))
                        .setAnchorView(binding.fab)
                        .show()
                }
            }).attachToRecyclerView(list)

            setHasOptionsMenu(true)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val searchOption = menu.findItem(R.id.option_search)

        searchOption.isVisible = false

        viewModel.binnedEntryCount.observe(viewLifecycleOwner) { entry ->
            searchOption.isVisible = entry != 0
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.bin_action_bar_menu, menu)

        val searchOption = menu.findItem(R.id.option_search)

        val pendingQuery = viewModel.currentSearchQuery.value

        if (pendingQuery.isNotEmpty()) {
            searchOption.expandActionView()
            searchActionView.setQuery(pendingQuery, false)
        }

        searchActionView = searchOption.actionView as SearchView

        /**
         * Verwendet die in Extensions.kt deklarierte Extension-Inline-Funktion 'onQueryTextChanged'
         * für ein SearchView-Objekt.
         *
         * Nutzereingabe im SearchView-Suchfeld ruft die Extension-Funktion auf, wodurch bei
         * Eingabe/Änderung der Eingabe der Suchstring in den MutableStateFlow 'currentSearchQuery'
         * des ViewModels geschrieben wird.
         * */
        searchActionView.onQueryChanged { query ->
            query.let { viewModel.currentSearchQuery.value = query }
        }
    }
}