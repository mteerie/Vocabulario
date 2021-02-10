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

    /**
     * Speichert und holt Sucheingabe des Nutzers in/aus state.
     * */
    private val currentSearchQuery = state.getLiveData("currentSearchQuery", "")

    fun setSearchQuery(query: String?) {
        currentSearchQuery.value = query
    }

    fun getSearchQuery(): String? {
        return currentSearchQuery.value
    }

    /**
     * Innerhalb dieses Flows wird der dataStoreFlow der Klasse DataStorePreferences zwischen-
     * gespeichert. Er wird im vocabularyEntries-Flow verwendet, um den Wert zu erhalten, der
     * derzeitig im val sortBy innerhalb des map-Operators in DataStorePreferences gespeichert ist.
     * */
    val userPrefFlow = preferences.dataStoreFlow

    private val scrollToTopVisibleState = MutableStateFlow(false)

    fun setScrollToTopVisible(state: Boolean) {
        scrollToTopVisibleState.value = state
    }

    val scrollToTopVisible = scrollToTopVisibleState.asLiveData()

    /**
     * Collector für currentSearchQuery. Der flatMapLatest-Operator ruft bei Änderung des Wertes
     * von currentSearchQuery eine Transformationsfunktion auf, die einen neuen Flow mit dem
     * zugehörigen Query und der im Konstruktor übergebenen Instanz von DataStorePreferences
     * (preferences) erzeugt.
     *
     * Das Query wird an die DAO-Funktion getAllEntries übergeben, um die Objekte
     * der Relation zu filtern. Darüber hinaus wird die Sortierung der Einträge übergeben,
     * welche aus den DataStorePreferences gezogen wird. Hiermit wird die Funktion des DAO auf-
     * gerufen, welche der übergebenen Sortierung entspricht.
     *
     * Bei erneuter Änderung von currentSearchQuery beendet und verwirft der Operator den zuvor
     * erzeugten Flow.
     * */
    @ExperimentalCoroutinesApi
    private val vocabularyEntries =
        combine(
            currentSearchQuery.asFlow(),
            userPrefFlow
        ) { query, preferences ->
            Pair(query, preferences)
        }.flatMapLatest { (query, preferences) ->
            dao.getAllEntries(query, preferences.sortBy)
        }

    @ExperimentalCoroutinesApi
    val allEntries = vocabularyEntries.asLiveData()

    val entryCount: LiveData<Int> = dao.countEntries().asLiveData()

    fun updateBinnedState(entry: Vocabulary, state: Boolean) = viewModelScope.launch {
        dao.update(entry.copy(binned = state))
    }

    fun onSortOptionSelected(option: SortBy) = viewModelScope.launch {
        preferences.updateSort(option)
    }

}

