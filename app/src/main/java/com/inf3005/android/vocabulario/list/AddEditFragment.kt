package com.inf3005.android.vocabulario.list

import android.os.Bundle
import android.view.View

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        }

    }
}