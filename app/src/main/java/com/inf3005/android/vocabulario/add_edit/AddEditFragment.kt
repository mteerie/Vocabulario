package com.inf3005.android.vocabulario.add_edit

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Difficulty
import com.inf3005.android.vocabulario.databinding.FragmentAddEditBinding
import androidx.core.widget.addTextChangedListener
import com.inf3005.android.vocabulario.utilities.KeyboardUtilities
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    private val viewModel: AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditBinding.bind(view)

        val spinnerItems = listOf(
            getString(R.string.difficulty_easy),
            getString(R.string.difficulty_intermediate),
            getString(R.string.difficulty_hard)
        )

        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, spinnerItems)

        binding.apply {

            viewModel.submitButtonState.observe(viewLifecycleOwner)
            {
                submitButton.isEnabled = it.first && it.second
            }

            inputDe.editText?.setText(viewModel.entryGermanValue)
            inputDe.editText?.addTextChangedListener {
                viewModel.entryGermanValue = it.toString()

                viewModel.setDeTextChangedState(
                    (viewModel.entryGermanValue.trim().isNotBlank()
                            && viewModel.entryGermanValue.trim().length <= 32)
                )
            }

            inputSp.editText?.setText(viewModel.entrySpanishValue)
            inputSp.editText?.addTextChangedListener {
                viewModel.entrySpanishValue = it.toString()

                viewModel.setSpTextChangedState(
                    (viewModel.entrySpanishValue.trim().isNotBlank()
                            && viewModel.entrySpanishValue.trim().length <= 32)
                )
            }

            (spinnerLayout.editText as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)

            (spinnerLayout.editText as? AutoCompleteTextView)?.setText(
                spinnerAdapter
                    .getItem(viewModel.entryDifficulty.ordinal - 1)
                    .toString(), false
            )

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
                viewModel.onClick()
                findNavController().popBackStack()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        /**
         * Die Tastatur bleibt ohne Aufruf der Funktion geöffnet, wenn der Nutzer beim Hinzufügen
         * eines Listeneintrags den submitButton drückt, ohne vorher die Tastatur zu schließen.
         * */
        KeyboardUtilities.hideKeyboard(requireActivity())

    }
}