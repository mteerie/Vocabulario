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

    // Analog zu ListViewModel
    private val currentBinSearchQuery = state.getLiveData("currentSearchQuery", "")

    fun setBinSearchQuery(query: String?) {
        currentBinSearchQuery.value = query
    }

    fun getBinSearchQuery(): String? {
        return currentBinSearchQuery.value
    }

    @ExperimentalCoroutinesApi
    private val binnedEntriesFlow =
        currentBinSearchQuery.asFlow().flatMapLatest { query ->
            dao.getAllBinnedEntries(query)
        }

    val binnedEntryCount: LiveData<Int> = dao.countBinnedEntries().asLiveData()

    @ExperimentalCoroutinesApi
    val binnedEntries = binnedEntriesFlow.asLiveData()

    fun updateBinnedState(entry: Vocabulary, state: Boolean) = viewModelScope.launch {
        dao.update(entry.copy(binned = state))
    }

    fun deleteBinnedEntries() = viewModelScope.launch {
        dao.clearBin()
    }
}