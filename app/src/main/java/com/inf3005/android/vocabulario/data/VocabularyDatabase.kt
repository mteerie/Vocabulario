package com.inf3005.android.vocabulario.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * Teile Room die Entitäten mit, welche Teil der Datenbank sein sollen.
 *
 * Erzeuge ein Callback, das die Datenbank im Auslieferungszustand mit Daten befüllt.
 *
 * Im Callback wird die Methode onCreate überschrieben -- er wird also nur bei erster Erzeugung der
 * Datenbank aufgerufen.
 * */
@Database(entities = [Vocabulary::class], version = 5)
abstract class VocabularyDatabase : RoomDatabase() {

    abstract fun vocabularyDao(): VocabularyDao

    class VocabularyDatabaseCallback @Inject constructor(
        private val vocabularyDao: Provider<VocabularyDao>,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = vocabularyDao.get()

            scope.launch {

                dao.clearList()

                dao.insert(Vocabulary("Auto", "coche", Difficulty.EASY))
                dao.insert(Vocabulary("Entwicklung", "desarrollo", Difficulty.INTERMEDIATE))
                dao.insert(Vocabulary(
                        "Schifffahrtskapitän",
                        "capitán de envío", Difficulty.HARD))
                dao.insert(Vocabulary(
                        "Papierkorb", "papelera", Difficulty.EASY, binned = true))
            }
        }
    }
}