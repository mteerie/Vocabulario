package com.inf3005.android.vocabulario.utilities

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VocabularyApplication : Application() {
    /**
    * "by lazy" wird verwendet um die Datenbank und das Repository nur dann zu erzeugen,
     * wenn diese auch gebraucht werden.
     *
     * Die Datenbank wird mittels der getInstance-Funktion erzeugt und erhält zusätzlich zu einem
     * Context auch einen Scope mitgeteilt.
    * */
//    val database by lazy { VocabularyDatabase.getInstance(this, CoroutineScope(SupervisorJob())) }
//    val repository by lazy { VocabularyRepository(database.vocabularyDao()) }

}