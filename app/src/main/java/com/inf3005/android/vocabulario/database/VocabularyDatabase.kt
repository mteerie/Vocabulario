package com.inf3005.android.vocabulario.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
* Der Room-Database-Code entspricht zu großen Teilen dem Boilerplate-Code in "Android CodeLab 6".
 * Erweiterung durch einen DatabaseCallback (zum Auffüllen der Datenbank im Auslieferungszustand)
 * und
* */

@Database(entities = [Vocabulary::class], version = 2, exportSchema = false)
abstract class VocabularyDatabase : RoomDatabase() {

    abstract fun vocabularyDao(): VocabularyDao

    private class VocabularyDatabaseCallback(
        private val scope: CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {

                    /**
                    * Dieser Codeblock dient dazu die Datenbank im Auslieferungszustand bereits
                     * befüllen zu können.
                     *
                     * Der Code wird nur bei Erzeugung der Datenbank aufgerufen, d.h. bei
                     * Installation der App auf einem Endgerät oder Emulator.
                    * */
                    val vocabularyDao = database.vocabularyDao()

                    vocabularyDao.clearList()

                    var entry = Vocabulary(1, "Auto", "coche")
                    vocabularyDao.insert(entry)

                    entry = Vocabulary(2, "Berg", "montaña")
                    vocabularyDao.insert(entry)

                    entry = Vocabulary(3, "Schwimmen", "nadar")
                    vocabularyDao.insert(entry)

                    entry = Vocabulary(4, "Pfannkuchen", "panqueques")
                    vocabularyDao.insert(entry)
                }
            }
        }

    }

    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope
        ): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabularyDatabase::class.java,
                    "vocabulary_database"
                )   .fallbackToDestructiveMigration()
                    .addCallback(VocabularyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance    // return instance falls INSTANCE == null
            }
        }
    }

}