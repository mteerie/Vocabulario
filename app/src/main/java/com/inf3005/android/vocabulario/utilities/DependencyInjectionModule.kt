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
 * Dieses Objekt dient dazu mittels Dagger + Hilt Dependency Injection innerhalb der App
 * verwenden zu können. Die hier erzeugten Funktionen (provideDatabase, ...) sind sogenannte
 * Provider, die bei Bedarf an anderen Stellen der App aufgerufen werden können. Sie stellen dann
 * den Code zur Verfügung - und zwar nur dann, wenn er auch wirklich benötigt wird.
 *
 * Darüber hinaus wird für die Funktionen provideDatabase, provideCoroutineScope und
 * provideDataStore die Singleton Annotation verwendet, um sicher zu stellen, dass lediglich
 * eine Instanz des jeweiligen Objekts über die gesamte App erzeugt wird.
 * */

@Module
@InstallIn(ApplicationComponent::class)
object DependencyInjectionModule {

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

    @Provides
    fun provideVocabularyDao(database: VocabularyDatabase) = database.vocabularyDao()

    @Singleton
    @Provides
    fun provideCoroutineScope() = CoroutineScope(
        Dispatchers.Main.immediate
                + SupervisorJob()
    )

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.createDataStore("preferences")
}