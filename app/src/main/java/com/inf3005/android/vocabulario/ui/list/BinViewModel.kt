package com.inf3005.android.vocabulario.ui.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class BinViewModel @ViewModelInject constructor(
    private val dao: VocabularyDao
) : ViewModel() {

    val currentSearchQuery = MutableStateFlow("")

    @ExperimentalCoroutinesApi
    private val binnedEntriesFlow =
        currentSearchQuery.flatMapLatest { query ->
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