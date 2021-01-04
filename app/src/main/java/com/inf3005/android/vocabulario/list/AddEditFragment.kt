package com.inf3005.android.vocabulario.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.databinding.FragmentAddEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    private val viewModel: AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditBinding.bind(view)

        binding.apply {
            inputDe.editText?.setText(viewModel.entryGermanValue)
            inputSp.editText?.setText(viewModel.entrySpanishValue)

            inputDe.editText?.addTextChangedListener {
                viewModel.entryGermanValue = it.toString()
            }

            inputSp.editText?.addTextChangedListener {
                viewModel.entrySpanishValue = it.toString()
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

            inputDe.editText?.addTextChangedListener(vocabularyTextWatcher)

            inputSp.editText?.addTextChangedListener(vocabularyTextWatcher)

            submitButton.setOnClickListener {
                viewModel.onClick()
                findNavController().popBackStack()
            }
        }

    }
}