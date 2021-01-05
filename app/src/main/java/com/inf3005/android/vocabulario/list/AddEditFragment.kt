package com.inf3005.android.vocabulario.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.database.Difficulty
import com.inf3005.android.vocabulario.databinding.FragmentAddEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit), AdapterView.OnItemSelectedListener {

    private val viewModel: AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditBinding.bind(view)

        val spinner = binding.difficultySpinner

        spinner.onItemSelectedListener = this

        binding.apply {


            inputDe.editText?.setText(viewModel.entryGermanValue)
            inputDe.editText?.addTextChangedListener {
                viewModel.entryGermanValue = it.toString()
            }

            inputSp.editText?.setText(viewModel.entrySpanishValue)
            inputSp.editText?.addTextChangedListener {
                viewModel.entrySpanishValue = it.toString()
            }

            ArrayAdapter.createFromResource(
                this.root.context,
                R.array.difficulty_choices,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.setSelection(viewModel.entryDifficulty.ordinal - 1)
            }

            val vocabularyTextWatcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val deEditText = inputDe.editText?.text.toString().trim()
                    val spEditText = inputSp.editText?.text.toString().trim()

                    submitButton.isEnabled = (deEditText.isNotEmpty() && spEditText.isNotEmpty()
                            && deEditText.length <= 32 && spEditText.length <= 32)

                    if (deEditText.length > 32)
                        inputDe.error = getString(R.string.error_input_too_long)
                    else {
                        inputDe.error = ""
                    }

                    if (spEditText.length > 32)
                        inputSp.error = getString(R.string.error_input_too_long)
                    else {
                        inputSp.error = ""
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            }

//            inputDe.editText?.addTextChangedListener(vocabularyTextWatcher)
//
//            inputSp.editText?.addTextChangedListener(vocabularyTextWatcher)

            submitButton.setOnClickListener {
                viewModel.onClick()

            }
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> viewModel.entryDifficulty = Difficulty.EASY
            1 -> viewModel.entryDifficulty = Difficulty.INTERMEDIATE
            2 -> viewModel.entryDifficulty = Difficulty.HARD
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}