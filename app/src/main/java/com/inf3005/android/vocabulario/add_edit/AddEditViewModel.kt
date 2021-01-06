package com.inf3005.android.vocabulario.add_edit

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.inf3005.android.vocabulario.data.Difficulty
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    var entryDifficulty = state.get<Difficulty>("entryDifficulty")
        ?: entry?.difficulty ?: Difficulty.EASY
        set(value) {
            field = value
            state.set("entryDifficulty", value)
        }

    private val _spTextChangedState = MutableStateFlow(entrySpanishValue.isNotBlank())

    var spTextChangedState = _spTextChangedState.asStateFlow()

    fun setSpTextChangedState(state: Boolean) {
        _spTextChangedState.value = state
    }

    private val _deTextChangedState = MutableStateFlow(entryGermanValue.isNotBlank())

    var deTextChangedState = _deTextChangedState.asStateFlow()

    fun setDeTextChangedState(state: Boolean) {
        _deTextChangedState.value = state
    }

    private val submitButtonStateFlow = combine(
        spTextChangedState,
        deTextChangedState
    ) { spTextChangedState, deTextChangedState ->
        Pair(spTextChangedState, deTextChangedState)
    }

    val submitButtonState = submitButtonStateFlow.asLiveData()

    fun onClick() {
        if (entry != null)
            update(
                entry.copy(
                    de = entryGermanValue, sp = entrySpanishValue,
                    difficulty = entryDifficulty
                )
            )
        else
            insert(Vocabulary(entryGermanValue, entrySpanishValue, entryDifficulty))
        return
    }

    private fun insert(entry: Vocabulary) = viewModelScope.launch {
        dao.insert(entry)
    }

    private fun update(entry: Vocabulary) = viewModelScope.launch {
        dao.update(entry)
    }
}