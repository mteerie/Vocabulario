package com.inf3005.android.vocabulario.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
* Der Room-Database-Code entspricht zu großen Teilen dem in "Android CodeLab 6" - hier wird auch erwähnt,
 * dass es sich bei Room-Datenbank-Instanziierung oft um Boilerplate-Code handelt.
* */

@Database(entities = [Vocabulary::class], version = 1, exportSchema = false)
abstract class VocabularyDatabase : RoomDatabase() {

    //abstract val vocabularyDao: VocabuldaryDao
    abstract fun vocabularyDao(): VocabularyDao

    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        fun getInstance(context: Context): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabularyDatabase::class.java,
                    "vocabulary_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance, falls INSTANCE == null
                instance
            }
        }
    }
}