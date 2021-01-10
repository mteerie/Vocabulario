package com.inf3005.android.vocabulario.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * Erzeuge Room-Database-Instanz - größtenteils Boilerplate Code.
 * */

@Database(entities = [Vocabulary::class], version = 4)
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

                /**
                 * Dieser Codeblock dient dazu die Datenbank im Auslieferungszustand bereits
                 * befüllen zu können.
                 *
                 * Der Code wird nur bei Erzeugung der Datenbank aufgerufen, d.h. bei
                 * Installation der App auf einem Endgerät oder Emulator.
                 * */

                dao.clearList()

                var entry = Vocabulary("Auto", "coche", Difficulty.EASY)
                dao.insert(entry)

                entry = Vocabulary("Berg", "montaña", Difficulty.EASY)
                dao.insert(entry)

                entry = Vocabulary(
                    "Finanzdienstleistungsunternehmen",
                    "empresa de servicios financieros", Difficulty.HARD
                )
                dao.insert(entry)

                entry = Vocabulary("Pfannkuchen", "panqueques", Difficulty.INTERMEDIATE)
                dao.insert(entry)
            }
        }
    }
}