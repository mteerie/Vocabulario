package com.inf3005.android.vocabulario.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyAdapter
import com.inf3005.android.vocabulario.databinding.FragmentListBinding
import com.inf3005.android.vocabulario.utilities.onQueryChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
            "Vokabel anpassen"
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
                    viewModel.delete(entry)

                    Snackbar.make(
                        requireView(),
                        getString(R.string.list_entry_deleted),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.list_entry_undo)) {
                            viewModel.insert(entry)
                        }
                        .setActionTextColor(ContextCompat.getColor(context!!, R.color.black))
                        .setTextColor(ContextCompat.getColor(context!!, R.color.black))
                        .setAnchorView(binding.fab)
                        .show()
                }
            }).attachToRecyclerView(list)

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
                    null, "Neue Vokabel"
                )
                findNavController().navigate(action, options)
            }

            viewModel.allEntries.observe(viewLifecycleOwner)
            {
                vocabularyAdapter.submitList(it)
            }

            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_bar_menu_list, menu)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_delete_all -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.delete_all_entries_title))
                    .setMessage(getString(R.string.delete_all_entries))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.delete_all_entries_positive)) { _, _ ->
                        viewModel.deleteAllEntries()

                        Toast.makeText(
                            context, getString(R.string.list_deleted), Toast.LENGTH_LONG
                        )
                            .show()
                    }
                    .setNegativeButton(getString(R.string.delete_all_entries_negative), null)
                    .show()
                true
            }

            R.id.sort_de -> {
                viewModel.entryOrder.value = SortBy.GERMAN
                true
            }

            R.id.sort_sp -> {
                viewModel.entryOrder.value = SortBy.SPANISH
                true
            }

            R.id.sort_difficulty_asc -> {
                viewModel.entryOrder.value = SortBy.DIFFICULTY_ASC
                true
            }

            R.id.sort_difficulty_desc -> {
                viewModel.entryOrder.value = SortBy.DIFFICULTY_DESC
                true
            }

            else -> super.onOptionsItemSelected(item)

        }
//        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
//                || super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val deleteAllButton = menu.findItem(R.id.option_delete_all)

        deleteAllButton.isVisible = false

        viewModel.entryCount.observe(viewLifecycleOwner) { entry ->
            deleteAllButton.isVisible = entry != 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        searchActionView.setOnQueryTextListener(null)
    }
}