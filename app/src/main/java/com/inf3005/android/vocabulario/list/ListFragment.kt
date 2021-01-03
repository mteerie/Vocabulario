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
import com.inf3005.android.vocabulario.database.Vocabulary
import com.inf3005.android.vocabulario.databinding.FragmentDialogBinding
import com.inf3005.android.vocabulario.databinding.FragmentListBinding
import com.inf3005.android.vocabulario.utilities.onQueryChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list), VocabularyAdapter.EntryClickListener {

    private val viewModel: ListViewModel by viewModels()

    private lateinit var dialogBinding: FragmentDialogBinding

//        override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        val binding: FragmentListBinding = DataBindingUtil.inflate(
//            inflater, R.layout.fragment_list, container, false,
//        )
//
//        val adapter = VocabularyAdapter(EntryClickListener { vocId ->
//            Toast.makeText(context, "$vocId", Toast.LENGTH_LONG)
//                .show()
//        })
//
//        binding.vocabularyViewModel = viewModel
//
//        binding.list.adapter = adapter
//
//        binding.list.layoutManager = LinearLayoutManager(activity)
//
//        viewModel.allEntries.observe(viewLifecycleOwner) { entry ->
//            entry.let { adapter.submitList(entry) }
//        }
//        /**
//         * onScrollListener für RecyclerView um FAB-Sichtbarkeit zu kontrollieren.
//         * */
//
//        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy > 0) {
//                    binding.fab.hide()
//                } else {
//                    binding.fab.show()
//                }
//                super.onScrolled(recyclerView, dx, dy)
//            }
//        })
//
//
//        /**
//         * Temporärer onClickListener für FAB um neue Vokabeln hinzufügen zu können.
//         * */
//
//        binding.fab.setOnClickListener {
//
//            dialogBinding = DataBindingUtil.inflate(
//                inflater, R.layout.fragment_dialog, container, false
//            )
//
//            val deInput = dialogBinding.inputDe
//            val spInput = dialogBinding.inputSp
//
//            val inputDialog = materialDialogBuilder.setView(dialogBinding.root)
//                .setTitle(getString(R.string.add_entry))
//                .setCancelable(false)
//                .setPositiveButton(getString(R.string.submit_button)) { dialog, _ ->
//
//                    val deText = deInput.editText?.editableText.toString()
//                    val spText = spInput.editText?.editableText.toString()
//
//                    viewModel.insert(Vocabulary(0, deText, spText))
//
//                    dialog.dismiss()
//
//                }
//                .setNegativeButton(getString(R.string.dismiss_button)) { dialog, _ ->
//                    deInput.clearOnEditTextAttachedListeners()
//                    spInput.clearOnEditTextAttachedListeners()
//                    dialog.cancel()
//                }
//                .show()
//
//            val submitButton = inputDialog.getButton(AlertDialog.BUTTON_POSITIVE)
//
//            submitButton.isEnabled = false
//
//            val vocabularyTextWatcher: TextWatcher = object : TextWatcher {
//                override fun beforeTextChanged(
//                    s: CharSequence,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//                }
//
//                @RequiresApi(Build.VERSION_CODES.O)
//                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                    val deEditText = deInput.editText?.text.toString().trim()
//                    val spEditText = spInput.editText?.text.toString().trim()
//
//                    submitButton.isEnabled = (deEditText.isNotEmpty() && spEditText.isNotEmpty()
//                            && deEditText.length <= 32 && spEditText.length <= 32)
//
//                    if (deEditText.length > 32)
//                        deInput.error = getString(R.string.error_input_too_long)
//                    else {
//                        deInput.error = ""
//                    }
//
//                    if (spEditText.length > 32)
//                        spInput.error = getString(R.string.error_input_too_long)
//                    else {
//                        spInput.error = ""
//                    }
//                }
//
//                override fun afterTextChanged(s: Editable) {}
//            }
//
//            deInput.editText?.addTextChangedListener(vocabularyTextWatcher)
//
//            spInput.editText?.addTextChangedListener(vocabularyTextWatcher)
//        }
//
//        setHasOptionsMenu(true)
//
//        return binding.root
//    }

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
                        popEnter = R.anim.slide_in_bottom
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
        val searchActionView = searchOption.actionView as SearchView

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
                    .setTitle(getString(R.string.delete_all_entries))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.delete_all_entries_positive)) { _, _ ->
                        viewModel.deleteAllEntries()

                        Toast.makeText(context, "Alle Einträge gelöscht.", Toast.LENGTH_LONG)
                            .show()
                    }
                    .setNegativeButton(getString(R.string.delete_all_entries_negative)) { dialog, _
                        ->
                        dialog.dismiss()
                    }
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
}