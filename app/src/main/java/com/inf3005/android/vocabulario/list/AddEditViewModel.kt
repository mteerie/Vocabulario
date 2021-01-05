package com.inf3005.android.vocabulario.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.inf3005.android.vocabulario.database.Difficulty
import com.inf3005.android.vocabulario.database.Vocabulary
import com.inf3005.android.vocabulario.database.VocabularyDao
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

    private val _spinnerSelectedState = MutableStateFlow(
        entryGermanValue.isNotBlank()
                && entrySpanishValue.isNotBlank()
    )

    var spinnerSelectedState = _spinnerSelectedState.asStateFlow()

    fun setSpinnerSelectedState(state: Boolean) {
        _spinnerSelectedState.value = state
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
        spinnerSelectedState,
        spTextChangedState,
        deTextChangedState
    ) { spinnerSelectedState, spTextChangedState, deTextChangedState ->
        Triple(spinnerSelectedState, spTextChangedState, deTextChangedState)
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