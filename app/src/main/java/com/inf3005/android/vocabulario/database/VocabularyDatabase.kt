package com.inf3005.android.vocabulario.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
* Der Room-Database-Code entspricht zu großen Teilen dem in "Android CodeLab 6" - hier wird auch erwähnt,
 * dass es sich bei Room-Datenbank-Instanziierung oft um Boilerplate-Code handelt.
* */

@Database(entities = [Vocabulary::class], version = 1, exportSchema = false)
abstract class VocabularyDatabase : RoomDatabase() {

    abstract fun vocabularyDao(): VocabularyDao

    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabularyDatabase::class.java,
                    "vocabulary_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(VocabularyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance, falls INSTANCE == null
                instance
            }
        }
    }

    private class VocabularyDatabaseCallback(private val scope: CoroutineScope)
        : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populate(database.vocabularyDao())
                }
            }
        }

        suspend fun populate(vocabularyDao: VocabularyDao) {
            vocabularyDao.clearList()

            var vocabulary = Vocabulary(0, "Hallo", "hola")
            vocabularyDao.insert(vocabulary)

            vocabulary = Vocabulary(1, "Schwimmbad", "piscina")
            vocabularyDao.insert(vocabulary)

            vocabulary = Vocabulary(2, "Berg", "montaña")
            vocabularyDao.insert(vocabulary)
        }
    }
}