package com.inf3005.android.vocabulario.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyDao
import com.inf3005.android.vocabulario.utilities.DataStorePreferences
import com.inf3005.android.vocabulario.utilities.SortBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListViewModel @ViewModelInject constructor(
    private val dao: VocabularyDao,
    private val preferences: DataStorePreferences
) : ViewModel() {

    /**
     * Zunächst ein leerer String, der mit der Eingabe der SearchView im ListFragment befüllt wird.
     *
     * Ein leerer String wird übergeben, sodass die Funktion getAllEntries für die RecyclerView
     * alle Einträge zurückgibt, ohne dass bereits etwas gefiltert wird.
     *
     * MutableStateFlow wird verwendet, weil er änderbar sein muss (Nutzereingabe, Mutable) und
     * sonst nur gebraucht wird, um seinen Collector (vocabularyEntries) über Änderungen in
     * Kenntnis zu setzen (StateFlow).
     * */
    val currentSearchQuery = MutableStateFlow("")

    /**
     * Innerhalb dieses Flows wird der dataStoreFlow der Klasse DataStorePreferences zwischen-
     * gespeichert. Er wird im vocabularyEntries-Flow verwendet, um den Wert zu erhalten, der
     * derzeitig im val sortBy innerhalb des map-Operators in DataStorePreferences gespeichert ist.
     * */
    val preferencesFlow = preferences.dataStoreFlow

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
            currentSearchQuery,
            preferencesFlow
        ) { query, preferences ->
            Pair(query, preferences)
        }
            .flatMapLatest { (query, preferences) ->
                dao.getAllEntries(query, preferences.sortBy)
            }

    @ExperimentalCoroutinesApi
    val allEntries = vocabularyEntries.asLiveData()

    val entryCount: LiveData<Int> = dao.countEntries().asLiveData()

    fun insert(entry: Vocabulary) = viewModelScope.launch {
        dao.insert(entry)
    }

    fun delete(entry: Vocabulary) = viewModelScope.launch {
        dao.delete(entry)
    }

    fun deleteAllEntries() = viewModelScope.launch {
        dao.clearList()
    }

    fun onSortOptionSelected(option: SortBy) = viewModelScope.launch {
        preferences.updateSort(option)
    }

}

