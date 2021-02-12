package com.inf3005.android.vocabulario.utilities

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import com.inf3005.android.vocabulario.data.VocabularyDatabase
import dagger.Module
import javax.inject.Singleton
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Dependency Injection mit Hilt. Details in der Dokumentation.
 * */
@Module
@InstallIn(ApplicationComponent::class)
object DependencyInjectionModule {

    /**
     * Erzeuge einzelne Instanz der Room-Datenbank. Wird in VocabularyDatabase verwendet.
     *
     * JournalMode.Truncate wird verwendet um die Datenbank als einzelne Datei speichern zu
     * können. Für zukünftige Backup-und-Import-Funktionalität gedacht.
     * */
    @Singleton
    @Provides
    fun provideDatabase(
        application: Application,
        callback: VocabularyDatabase.VocabularyDatabaseCallback
    ) =
        Room.databaseBuilder(
            application, VocabularyDatabase::class.java,
            "vocabulary_database"
        )
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()

    // Erzeuge bei Injektion Instanz des DAO.
    @Provides
    fun provideDao(database: VocabularyDatabase) = database.vocabularyDao()

    // Stelle ein CoroutineScope bereit, das in VocabularyDatabase für DatabaseCallback ben. wird.
    @Singleton
    @Provides
    fun provideScope() = CoroutineScope(SupervisorJob())

    // Erzeuge einzelne Instanz des DataStore. In Konstruktor von DataStorePreferences injiziert.
    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.createDataStore("preferences")
}