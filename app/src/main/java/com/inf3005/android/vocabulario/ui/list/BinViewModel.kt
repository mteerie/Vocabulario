package com.inf3005.android.vocabulario.ui.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class BinViewModel @ViewModelInject constructor(
    private val dao: VocabularyDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    // Analog zu ListViewModel.
    private val currentBinSearchQuery = state.getLiveData("currentSearchQuery", "")

    fun setBinSearchQuery(query: String?) {
        currentBinSearchQuery.value = query
    }

    fun getBinSearchQuery(): String? = currentBinSearchQuery.value

    // Analog zu ListViewModel - hier nur Übergabe einer Suchfeldeingabe als Parameter.

    @ExperimentalCoroutinesApi
    private val binnedEntriesFlow =
        currentBinSearchQuery.asFlow().flatMapLatest {
            dao.getAllBinnedEntries(it)
        }

    // Zähle Einträge im Papierkorb - für Observer in BinFramgent.
    val binnedEntryCount: LiveData<Int> = dao.countBinnedEntries().asLiveData()

    // Flow als LiveData speichern - für Observer in BinFragment.
    @ExperimentalCoroutinesApi
    val binnedEntries = binnedEntriesFlow.asLiveData()

    // Zum Wiederherstellen aus dem Papierkorb - setzt binned auf false für den geklickten Eintrag.
    fun updateBinnedState(entry: Vocabulary, state: Boolean) = viewModelScope.launch {
        dao.update(entry.copy(binned = state))
    }

    // Lösche alle Einträge für die binned true ist.
    fun deleteBinnedEntries() = viewModelScope.launch {
        dao.clearBin()
    }
}