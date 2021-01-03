package com.inf3005.android.vocabulario.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.inf3005.android.vocabulario.database.Vocabulary
import com.inf3005.android.vocabulario.database.VocabularyDao

class AddEditViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val dao: VocabularyDao
) : ViewModel() {
    val entry = state.get<Vocabulary>("entry")

    var entryGermanValue = state.get<String>("entryGermanValue") ?: entry?.de ?: ""
        set(value) {
            field = value
            state.set("entryGermanValue", value)
        }

    var entrySpanishValue = state.get<String>("entrySpanishValue") ?: entry?.sp ?: ""
        set(value) {
            field = value
            state.set("entrySpanishValue", value)
        }
}