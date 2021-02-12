package com.inf3005.android.vocabulario.ui.add_edit

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Difficulty
import com.inf3005.android.vocabulario.databinding.FragmentAddEditBinding
import com.inf3005.android.vocabulario.utilities.KeyboardUtilities
import com.inf3005.android.vocabulario.utilities.NavigationDrawerState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {
    private val viewModel: AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Erlaube dem Nutzer nicht den Navigation Drawer zu öffnen.
        (activity as NavigationDrawerState).setDrawerState(false)

        val binding = FragmentAddEditBinding.bind(view)

        // Wird mit spinnerAdapter verwendet, um Schwierigkeitsgrade in einem Dropdown anzuzeigen.
        val spinnerItems = listOf(
            getString(R.string.difficulty_easy),
            getString(R.string.difficulty_intermediate),
            getString(R.string.difficulty_hard)
        )

        // Adapter für spinnerLayout.
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, spinnerItems)

        binding.apply {

            // Observer entscheidet über submitButton-Status, abhängig von StateFlows im ViewModel.
            viewModel.submitButtonState.observe(viewLifecycleOwner)
            {
                submitButton.isEnabled = it.first && it.second
            }

            // Text im Textfeld wird entsprechend der Variable im ViewModel gesetzt.
            inputDe.editText?.setText(viewModel.entryGermanValue)

            // Setze bei Änderung der Texteingabe die Variable im ViewModel.
            inputDe.editText?.addTextChangedListener {
                viewModel.entryGermanValue = it.toString()

                // Eingabe gilt nur als gültig wenn sie nicht leer oder länger als 32 Zeichen ist.
                viewModel.setDeTextValidState(
                    (viewModel.entryGermanValue.trim().isNotBlank()
                            && viewModel.entryGermanValue.trim().length <= 32)
                )
            }

            // Siehe Zeile 48ff.
            inputSp.editText?.setText(viewModel.entrySpanishValue)

            inputSp.editText?.addTextChangedListener {
                viewModel.entrySpanishValue = it.toString()

                viewModel.setSpTextValidState(
                    (viewModel.entrySpanishValue.trim().isNotBlank()
                            && viewModel.entrySpanishValue.trim().length <= 32)
                )
            }

            // Verknüpfe spinnerLayout als AutoCompleteTextView mit Adapter.
            (spinnerLayout.editText as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)

            /**
             * Setze die Auswahl des Dropdown-Menü entsprechend der Schwierigkeit, die im ViewModel
             * gespeichert ist.
             *
             * filter: false ist notwendig, da sonst die Auswahloptionen des Dropdown-Menü auf
             * die über getItem().toString() gesetzte Schwierigkeit beschränkt wird.
             * */
            (spinnerLayout.editText as? AutoCompleteTextView)?.setText(
                spinnerAdapter
                    .getItem(viewModel.entryDifficulty.ordinal - 1)
                    .toString(), false
            )

            // Passe die Variable im ViewModel bei Änderung der Auswahl im Dropdown an.
            spinnerLayout.editText?.addTextChangedListener {
                when (it.toString()) {
                    getString(R.string.difficulty_easy) -> viewModel
                        .entryDifficulty = Difficulty.EASY
                    getString(R.string.difficulty_intermediate) -> viewModel
                        .entryDifficulty = Difficulty.INTERMEDIATE
                    getString(R.string.difficulty_hard) -> viewModel
                        .entryDifficulty = Difficulty.HARD
                }
            }

            submitButton.setOnClickListener {
                viewModel.onSubmitClick()
                findNavController().popBackStack()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        /**
         * Verstecke die Tastatur, wenn der Nutzer die App pausiert oder das Fragment beendet.
         * Soll bereits in onPause geschehen, weil es dann besser aussieht.
         */
        KeyboardUtilities.hideKeyboard(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Erlaube dem Nutzer wieder den Navigation Drawer zu öffnen.
        (activity as NavigationDrawerState).setDrawerState(true)
    }
}