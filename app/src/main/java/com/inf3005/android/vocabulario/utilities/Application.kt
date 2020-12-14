package com.inf3005.android.vocabulario.utilities

import android.app.Application
import com.inf3005.android.vocabulario.database.VocabularyDatabase
import com.inf3005.android.vocabulario.database.VocabularyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class VocabularyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { VocabularyDatabase.getInstance(this, applicationScope) }
    val repository by lazy { VocabularyRepository(database.vocabularyDao()) }

}