package com.inf3005.android.vocabulario.ui.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyDao
import com.inf3005.android.vocabulario.utilities.DataStorePreferences
import com.inf3005.android.vocabulario.utilities.SortBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ListViewModel @ViewModelInject constructor(
    private val dao: VocabularyDao,
    private val preferences: DataStorePreferences,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    // Speichert und holt Sucheingabe des Nutzers in/aus state.
    private val currentSearchQuery = state.getLiveData("currentSearchQuery", "")

    // Funktionen um currentSearchQuery zu setzen oder den Wert abzufragen.
    fun setSearchQuery(query: String?) {
        currentSearchQuery.value = query
    }

    fun getSearchQuery(): String? = currentSearchQuery.value

    // ListFragment speichert bei Aufruf der TTS-Ausprache das gesprochene Wort in dieser Variable.
    private var lastSpokenText = ""

    fun setSpokenText(text: String) {
        lastSpokenText = text
    }

    // In ListFragment/onSwiped, um zu prüfen, ob das gelöschte Wort gerade ausgesprochen wird.
    fun getLastSpokenText(): String = lastSpokenText

    /**
     * Speichert den dataStoreFlow aus preferences.
     *
     * Wird für vocabularyEntries benötigt, um korrekte Sortieroption an dao zu übermitteln
     * */
    val userPrefFlow = preferences.dataStoreFlow

    /**
     * Kann dynamisch in ListFragment durch Aufruf von setScrollToTopVisible angepasst werden.
     *
     * scrollToTopVisible bildet den Flow als LiveData ab, damit er von einem Observer im
     * ListFragment beobachtet werden kann.
     */
    private val scrollToTopVisibleStateFlow = MutableStateFlow(false)

    fun setScrollToTopVisible(state: Boolean) {
        scrollToTopVisibleStateFlow.value = state
    }

    val scrollToTopVisible = scrollToTopVisibleStateFlow.asLiveData()

    /**
     * Kombiniert die Flows currentSearchQuery und userPrefFlow zu einem Pair.
     *
     * Mit dem Pair wird der flatMapLatest-Operator aufgerufen, der es entpackt und mit den
     * Einzelteilen eine DAO-Funktion aufruft, um relevante Datensätze zu erhalten.
     * */
    @ExperimentalCoroutinesApi
    private val vocabularyEntries =
        combine(
            currentSearchQuery.asFlow(),
            userPrefFlow
        ) { query, preferences ->
            Pair(query, preferences)
        }.flatMapLatest {
            dao.getAllEntries(it.first, it.second.sortBy)
        }

    /**
     * Den FLow vocabularyEntries als LiveData speichern, um ihn mit Observer in ListFragment
     * verwenden zu können.
     * */
    @ExperimentalCoroutinesApi
    val allEntries = vocabularyEntries.asLiveData()

    // Zähle Einträge der Datenbank, die nicht im Papierkorb sind. Für Observer in ListFragment.
    val entryCount: LiveData<Int> = dao.countEntries().asLiveData()

    // Für Wischgesten in ListFragment: Setze binned für zugehörigen Eintrag auf true.
    fun updateBinnedState(entry: Vocabulary, state: Boolean) = viewModelScope.launch {
        dao.update(entry.copy(binned = state))
    }

    // Coroutine starten um suspend-Funktion ausführen zu können - für Update der Sortieroption.
    fun onSortOptionSelected(option: SortBy) = viewModelScope.launch {
        preferences.updateSort(option)
    }
}

