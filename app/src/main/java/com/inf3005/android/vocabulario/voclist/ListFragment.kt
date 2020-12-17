package com.inf3005.android.vocabulario.voclist

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.TypefaceSpan
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.database.Vocabulary
import com.inf3005.android.vocabulario.utilities.VocabularyApplication


class ListFragment : Fragment() {

    private lateinit var fab: FloatingActionButton

    private lateinit var DialogLayout: View

    private lateinit var deInput: TextInputLayout

    private lateinit var spInput: TextInputLayout

    private lateinit var materialDialogBuilder: MaterialAlertDialogBuilder

    val viewModel: VocabularyViewModel by viewModels {
        VocabularyViewModelFactory(
            (requireNotNull(this.activity)
                .application as VocabularyApplication).repository
        )
    }

    val adapter = VocabularyAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.allEntries.observe(viewLifecycleOwner) { entry ->
            entry.let { adapter.submitList(entry) }
        }

        materialDialogBuilder = MaterialAlertDialogBuilder(requireContext())

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

            /**
             * Überschreiben der onSwiped-Funktion, um festzulegen was bei Ausführen einer
             * Geste geschehen soll.
             *
             * Die Hilfsfunktion getEntryAt aus dem VocabularyAdapter wird aufgerufen,
             * um den Listeneintrag an der angegebenen Position zu bestimmen und in entry
             * zu speichern.
             *
             * Der Listeneintrag wird mittels delete-Methode aus dem ViewModel entfernt.
             *
             * Zuletzt wird eine Snackbar angezeigt, die den Nutzer darüber informiert, dass
             * ein Listeneintrag entfernt wurde. Zusätzlich wird auch eine Aktion verfügbar,
             * mit deren Hilfe der Nutzer den Löschvorgang rückgängig machen kann.
             *
             * Entscheidet sich der Nutzer dazu den Listeneintrag widerherzustellen, so fügt ihn
             * die insert-Methode des ViewModels an der korrekten Position wieder ein.
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val entry = adapter.getEntryAt(viewHolder.adapterPosition)
                viewModel.delete(entry)
                Snackbar.make(view, getString(R.string.list_entry_deleted), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.list_entry_undo)) { viewModel.insert(entry) }
                    .setActionTextColor(ContextCompat.getColor(context!!, R.color.black))
                    .setTextColor(ContextCompat.getColor(context!!, R.color.black))
                    .show()
            }
        }).attachToRecyclerView(recyclerView)



        fab = view.findViewById(R.id.fab)

        fab.setOnClickListener {

            DialogLayout = inflater.inflate(
                R.layout.fragment_dialog,
                container,
                false
            )

            deInput = this.DialogLayout.findViewById(R.id.input_de)
            spInput = this.DialogLayout.findViewById(R.id.input_sp)

            val inputDialog = materialDialogBuilder.setView(this.DialogLayout)
                .setTitle(getString(R.string.add_entry))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.submit_button)) { dialog, _ ->

                    val deText = deInput.editText?.editableText.toString()
                    val spText = spInput.editText?.editableText.toString()

                    viewModel.insert(Vocabulary(0, deText, spText))

                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.dismiss_button)) { dialog, _ ->
                    deInput.clearOnEditTextAttachedListeners()
                    spInput.clearOnEditTextAttachedListeners()
                    dialog.dismiss()
                }
                .show()

            val submitButton = inputDialog.getButton(AlertDialog.BUTTON_POSITIVE)

            submitButton.isEnabled = false

            val vocabularyTextWatcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val deEditText = deInput.editText?.text.toString().trim()
                    val spEditText = spInput.editText?.text.toString().trim()

                    submitButton.isEnabled = (deEditText.isNotEmpty() && spEditText.isNotEmpty()
                            && deEditText.length <= 32 && spEditText.length <= 32)

                    if (deEditText.length > 32)
                        deInput.error = getString(R.string.error_input_too_long)
                    else { deInput.error = "" }

                    if (spEditText.length > 32)
                        spInput.error = getString(R.string.error_input_too_long)
                    else { spInput.error = "" }
                }

                override fun afterTextChanged(s: Editable) {}
            }

            deInput.editText?.addTextChangedListener(vocabularyTextWatcher)

            spInput.editText?.addTextChangedListener(vocabularyTextWatcher)
        }

        setHasOptionsMenu(true)

        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_bar_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.option_delete_all) {
            AlertDialog.Builder(requireNotNull(context))
                .setMessage(getString(R.string.delete_all_entries))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.delete_all_entries_positive)) { _, _ ->
                    viewModel.deleteAllEntries()
                }
                .setNegativeButton(getString(R.string.delete_all_entries_negative)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
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