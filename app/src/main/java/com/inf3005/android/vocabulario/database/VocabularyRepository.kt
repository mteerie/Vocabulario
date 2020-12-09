package com.inf3005.android.vocabulario.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class VocabularyRepository(private val vocabularyDao: VocabularyDao) {

    val allEntries: Flow<List<Vocabulary>> = vocabularyDao.getAllEntries()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(vocabulary: Vocabulary) {
        vocabularyDao.insert(vocabulary)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(vocabulary: Vocabulary) {
        vocabularyDao.update(vocabulary)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(vocabulary: Vocabulary) {
        vocabularyDao.delete(vocabulary)
    }
}