package com.inf3005.android.vocabulario.ui.add_edit

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

    // Speichere das Parcelable "entry" aus der Navigationsaktion - kann null sein.
    val entry = state.get<Vocabulary>("entry")

    /**
     * Versuche zunächst die Variablen mit Werten aus state zu befüllen. Ist dies nicht möglich,
     * versuche sie mit den Attributen von entry zu befüllen. Ist entry null, weise
     * entsprechende Werte zu.
     * */
    var entryGermanValue = state.get<String>("de") ?: entry?.de ?: ""
        set(value) {
            field = value
            state.set("de", value)
        }

    var entrySpanishValue = state.get<String>("sp") ?: entry?.sp ?: ""
        set(value) {
            field = value
            state.set("sp", value)
        }

    var entryDifficulty = state.get<Difficulty>("difficulty")
        ?: entry?.difficulty ?: Difficulty.EASY
        set(value) {
            field = value
            state.set("difficulty", value)
        }

    /**
     * StateFlows, die für die Entscheidung über den Status des submitButton in AddEditFragment
     * benötigt werden.
     * */
    private val _spTextValidState = MutableStateFlow(entrySpanishValue.isNotBlank())

    var spTextValidState = _spTextValidState.asStateFlow()

    fun setSpTextValidState(state: Boolean) {
        _spTextValidState.value = state
    }

    private val _deTextValidState = MutableStateFlow(entryGermanValue.isNotBlank())

    var deTextValidState = _deTextValidState.asStateFlow()

    fun setDeTextValidState(state: Boolean) {
        _deTextValidState.value = state
    }

    private val submitButtonStateFlow = combine(
        spTextValidState,
        deTextValidState
    ) { spTextValidState, deTextValidState ->
        Pair(spTextValidState, deTextValidState)
    }

    // Wird von Observer in AddEditFragment verwendet.
    val submitButtonState = submitButtonStateFlow.asLiveData()

    fun onSubmitClick() {
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

    // In onSubmitClick verwendet - fügt neuen Eintrag hinzu.
    private fun insert(entry: Vocabulary) = viewModelScope.launch {
        dao.insert(entry)
    }

    // In onSubmitClick verwendet - passt vorhandenen Eintrag an.
    private fun update(entry: Vocabulary) = viewModelScope.launch {
        dao.update(entry)
    }
}