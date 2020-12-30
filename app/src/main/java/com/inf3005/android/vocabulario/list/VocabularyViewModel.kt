package com.inf3005.android.vocabulario.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.inf3005.android.vocabulario.database.Vocabulary
import com.inf3005.android.vocabulario.database.VocabularyDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VocabularyViewModel @ViewModelInject constructor(
    private val dao: VocabularyDao
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
     * Collector für currentSearchQuery. Der flatMapLatest-Operator ruft bei Änderung des Wertes
     * von currentSearchQuery eine Transformationsfunktion auf
     * (innerhalb der geschweiften Klammern), die einen neuen Flow mit dem zugehörigen Query
     * erzeugt.
     *
     * Das Query wird an die DAO-Funktion getAllEntries übergeben, um die Objekte
     * der Relation zu filtern.
     *
     * Bei erneuter Änderung von currentSearchQuery beendet und verwirft der Operator den zuvor
     * erzeugten Flow.
     * */
    @ExperimentalCoroutinesApi
    private val vocabularyEntries = currentSearchQuery.flatMapLatest { query ->
        query.let { dao.getAllEntries(query) }
    }

    @ExperimentalCoroutinesApi
    val allEntries = vocabularyEntries.asLiveData()

//    val allEntries: LiveData<List<Vocabulary>> = dao.getAllEntries("").asLiveData()

    val entryCount: LiveData<Int> = dao.countEntries().asLiveData()

    fun insert(entry: Vocabulary) = viewModelScope.launch {
        dao.insert(entry)
    }

    fun update(entry: Vocabulary) = viewModelScope.launch {
        dao.update(entry)
    }

    fun delete(entry: Vocabulary) = viewModelScope.launch {
        dao.delete(entry)
    }

    fun deleteAllEntries() = viewModelScope.launch {
        dao.clearList()
    }

}

//class VocabularyViewModelFactory(
//    private val repository: VocabularyRepository
//    ) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(VocabularyViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return VocabularyViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}