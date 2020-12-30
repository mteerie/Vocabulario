package com.inf3005.android.vocabulario.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * Erzeuge Room-Database-Instanz - größtenteils Boilerplate Code.
 * */

@Database(entities = [Vocabulary::class], version = 3, exportSchema = false)
abstract class VocabularyDatabase : RoomDatabase() {

    abstract fun vocabularyDao(): VocabularyDao

    class VocabularyDatabaseCallback @Inject constructor(
        private val database: Provider<VocabularyDatabase>,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().vocabularyDao()

            scope.launch {

                /**
                 * Dieser Codeblock dient dazu die Datenbank im Auslieferungszustand bereits
                 * befüllen zu können.
                 *
                 * Der Code wird nur bei Erzeugung der Datenbank aufgerufen, d.h. bei
                 * Installation der App auf einem Endgerät oder Emulator.
                 * */

                dao.clearList()

                var entry = Vocabulary("Auto", "coche")
                dao.insert(entry)

                entry = Vocabulary("Berg", "montaña")
                dao.insert(entry)

                entry = Vocabulary("Finanzdienstleistungsunternehmen",
                    "empresa de servicios financieros")
                dao.insert(entry)

                entry = Vocabulary("Pfannkuchen", "panqueques")
                dao.insert(entry)
            }
        }
    }

}

//    companion object {
//        @Volatile
//        private var INSTANCE: VocabularyDatabase? = null
//
//        fun getInstance(
//            context: Context,
//            scope: CoroutineScope
//        ): VocabularyDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    VocabularyDatabase::class.java,
//                    "vocabulary_database"
//                ).fallbackToDestructiveMigration()
//                    .addCallback(VocabularyDatabaseCallback(scope))
//                    .build()
//                INSTANCE = instance
//                instance    // return instance falls INSTANCE == null
//            }
//        }
//    }

//}