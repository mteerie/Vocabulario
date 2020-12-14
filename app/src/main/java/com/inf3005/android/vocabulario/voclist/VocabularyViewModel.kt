package com.inf3005.android.vocabulario.voclist

import androidx.lifecycle.*
import com.inf3005.android.vocabulario.database.Vocabulary
import com.inf3005.android.vocabulario.database.VocabularyRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class VocabularyViewModel(private val repository: VocabularyRepository) : ViewModel() {

    val allEntries: LiveData<List<Vocabulary>> = repository.allEntries.asLiveData()

    fun insert(entry: Vocabulary) = viewModelScope.launch {
        repository.insert(entry)
    }

    fun update(entry: Vocabulary) = viewModelScope.launch {
        repository.update(entry)
    }

    fun delete(entry: Vocabulary) = viewModelScope.launch {
        repository.delete(entry)
    }
}

class VocabularyViewModelFactory(
    private val repository: VocabularyRepository
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocabularyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VocabularyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}