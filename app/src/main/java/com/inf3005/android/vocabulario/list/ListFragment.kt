package com.inf3005.android.vocabulario.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyAdapter
import com.inf3005.android.vocabulario.databinding.FragmentListBinding
import com.inf3005.android.vocabulario.utilities.SortBy
import com.inf3005.android.vocabulario.utilities.onQueryChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list), VocabularyAdapter.EntryClickListener {

    private lateinit var searchActionView: SearchView

    private val viewModel: ListViewModel by viewModels()

    override fun onClick(entry: Vocabulary) {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        val action = ListFragmentDirections.actionListFragmentToAddEditFragment(
            entry,
            getString(R.string.add_edit_title)
        )
        findNavController().navigate(action, options)
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val binding = FragmentListBinding.bind(view)

        val vocabularyAdapter = VocabularyAdapter(this)

        binding.apply {
            list.apply {
                adapter = vocabularyAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewModel.entryCount.observe(viewLifecycleOwner) { entry ->
                binding.emptyListText.isVisible = entry == 0
            }

            binding.fab.setOnClickListener {
                val options = navOptions {
                    anim {
                        enter = R.anim.slide_in_bottom
                        exit = R.anim.fade_out
                        popEnter = R.anim.slide_in_top
                        popExit = R.anim.fade_out
                    }
                }

                val action = ListFragmentDirections.actionListFragmentToAddEditFragment(
                    null, getString(R.string.add_entry)
                )
                findNavController().navigate(action, options)
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
                    viewModel.updateBinnedState(entry, state = true)

                    Snackbar.make(
                        requireView(),
                        getString(R.string.list_entry_moved_to_bin),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.list_entry_undo)) {
                            viewModel.updateBinnedState(entry, state = false)
                        }
                        .setActionTextColor(ContextCompat.getColor(context!!, R.color.black))
                        .setTextColor(ContextCompat.getColor(context!!, R.color.black))
                        .setAnchorView(binding.fab)
                        .show()
                }
            }).attachToRecyclerView(list)

            viewModel.allEntries.observe(viewLifecycleOwner)
            {
                vocabularyAdapter.submitList(it)
            }

            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_action_bar_menu, menu)

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

        /**
         * Der first-Operator gibt den ersten Wert des Flows zurück und verwirft ihn anschließend.
         *
         * Somit kann der Wert sortBy, der in der Data Class PreferenceProperties angesprochen
         * werden. Mithilfe dieses Werts kann beim Erzeugen des Optionsmenü derjenige Radio-Button
         * ausgewählt werden, welcher der vom Nutzer ausgewählten und im DataStore gespeicherten
         * Sortierung entspricht.
         * */
        viewLifecycleOwner.lifecycleScope.launch {
            when (viewModel.preferencesFlow.first().sortBy) {
                SortBy.GERMAN -> menu.findItem(R.id.sort_de).isChecked = true
                SortBy.SPANISH -> menu.findItem(R.id.sort_sp).isChecked = true
                SortBy.DIFFICULTY_ASC -> menu.findItem(R.id.sort_difficulty_asc).isChecked = true
                SortBy.DIFFICULTY_DESC -> menu.findItem(R.id.sort_difficulty_desc).isChecked = true
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            R.id.option_delete_all -> {
//                MaterialAlertDialogBuilder(requireContext())
//                    .setTitle(getString(R.string.delete_all_entries_title))
//                    .setMessage(getString(R.string.delete_all_entries))
//                    .setCancelable(true)
//                    .setPositiveButton(getString(R.string.delete_all_entries_positive)) { _, _ ->
//                        viewModel.deleteAllEntries()
//
//                        Toast.makeText(
//                            context, getString(R.string.list_deleted), Toast.LENGTH_LONG
//                        )
//                            .show()
//                    }
//                    .setNegativeButton(getString(R.string.delete_all_entries_negative), null)
//                    .show()
//                true
//            }

            R.id.sort_de -> {
                viewModel.onSortOptionSelected(SortBy.GERMAN)
                item.isChecked = true
                true
            }

            R.id.sort_sp -> {
                viewModel.onSortOptionSelected(SortBy.SPANISH)
                item.isChecked = true
                true
            }

            R.id.sort_difficulty_asc -> {
                viewModel.onSortOptionSelected(SortBy.DIFFICULTY_ASC)
                item.isChecked = true
                true
            }

            R.id.sort_difficulty_desc -> {
                viewModel.onSortOptionSelected(SortBy.DIFFICULTY_DESC)
                item.isChecked = true
                true
            }

            else -> super.onOptionsItemSelected(item)

        }
//        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
//                || super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val searchOption = menu.findItem(R.id.option_search)

        searchOption.isVisible = false

        viewModel.entryCount.observe(viewLifecycleOwner) { entry ->
            searchOption.isVisible = entry != 0
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()

        searchActionView.setOnQueryTextListener(null)
    }
}