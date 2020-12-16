package com.inf3005.android.vocabulario.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow

class VocabularyRepository(private val vocabularyDao: VocabularyDao) {

    val allEntries: Flow<List<Vocabulary>> = vocabularyDao.getAllEntries()
    val entryCount: Flow<Int> = vocabularyDao.countEntries()

    @WorkerThread
    suspend fun insert(entry: Vocabulary) {
        vocabularyDao.insert(entry)
    }

    @WorkerThread
    suspend fun update(entry: Vocabulary) {
        vocabularyDao.update(entry)
    }

    @WorkerThread
    suspend fun delete(entry: Vocabulary) {
        vocabularyDao.delete(entry)
    }

    @WorkerThread
    suspend fun deleteAllEntries() {
        vocabularyDao.clearList()
    }
}