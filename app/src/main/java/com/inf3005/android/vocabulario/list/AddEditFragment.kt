package com.inf3005.android.vocabulario.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.database.Difficulty
import com.inf3005.android.vocabulario.databinding.FragmentAddEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    private val viewModel: AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditBinding.bind(view)

        val spinnerItems = listOf("Einfach", "Fortgeschritten", "Schwierig")

        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, spinnerItems)

        if (viewModel.entryGermanValue != "")
            viewModel.setDeTextChangedState(true)

        if (viewModel.entrySpanishValue != "")
            viewModel.setSpTextChangedState(true)

        binding.apply {

            viewModel.submitButtonState.observe(viewLifecycleOwner)
            {
                submitButton.isEnabled = it.first && it.second && it.third
            }

            inputDe.editText?.setText(viewModel.entryGermanValue)
            inputDe.editText?.addTextChangedListener {
                viewModel.entryGermanValue = it.toString()

                if (viewModel.entryGermanValue != "")
                    viewModel.setDeTextChangedState(true)
                else viewModel.setDeTextChangedState(false)
            }

            inputSp.editText?.setText(viewModel.entrySpanishValue)
            inputSp.editText?.addTextChangedListener {
                viewModel.entrySpanishValue = it.toString()

                if (viewModel.entrySpanishValue != "")
                    viewModel.setSpTextChangedState(true)
                else viewModel.setSpTextChangedState(false)
            }

            (spinnerLayout.editText as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)

            val spinnerText = when (viewModel.entryDifficulty) {
                Difficulty.INTERMEDIATE -> "Fortgeschritten"
                Difficulty.HARD -> "Schwierig"
                else -> "Einfach"
            }

            spinnerLayout.hint = spinnerText

            spinnerLayout.editText?.setOnClickListener {
                spinnerLayout.hint = "Schwierigikeit"
            }

            spinnerLayout.editText?.addTextChangedListener {
                when (it.toString()) {
                    "Einfach" -> viewModel.entryDifficulty = Difficulty.EASY
                    "Fortgeschritten" -> viewModel.entryDifficulty = Difficulty.INTERMEDIATE
                    "Schwierig" -> viewModel.entryDifficulty = Difficulty.HARD
                }
                viewModel.setSpinnerSelectedState(true)
            }

            submitButton.setOnClickListener {
                viewModel.onClick()
                findNavController().popBackStack()
            }
        }

//        val vocabularyTextWatcher: TextWatcher = object : TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//                if(viewModel.entryGermanValue.trim().isNotEmpty()
//                    && viewModel.entrySpanishValue.trim().isNotEmpty())
//                        viewModel.setTextChangedState(true)
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//                binding.submitButton.isEnabled = (
//                        viewModel.entryGermanValue.trim().isNotEmpty()
//                                && viewModel.entrySpanishValue.trim().isNotEmpty()
//                                && viewModel.entryGermanValue.length <= 32
//                                && viewModel.entrySpanishValue.length <= 32
//                        )
//
//
//                if (viewModel.entryGermanValue.length > 32)
//                    binding.inputDe.error = getString(R.string.error_input_too_long)
//                else binding.inputDe.error = ""
//
//                if (viewModel.entrySpanishValue.length > 32)
//                    binding.inputSp.error = getString(R.string.error_input_too_long)
//                else binding.inputSp.error = ""
//
//                viewModel.setTextChangedState(true)
//
//            }
//
//            override fun afterTextChanged(s: Editable) {}
//        }
//
//        binding.inputDe.editText?.addTextChangedListener(vocabularyTextWatcher)
//
//        binding.inputSp.editText?.addTextChangedListener(vocabularyTextWatcher)

    }
}