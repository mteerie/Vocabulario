package com.inf3005.android.vocabulario.utilities

import android.app.Application
import androidx.room.Room
import com.inf3005.android.vocabulario.data.VocabularyDatabase
import dagger.Module
import javax.inject.Singleton
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob



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
            .build()

    @Provides
    fun provideVocabularyDao(database: VocabularyDatabase) = database.vocabularyDao()

    @Singleton
    @Provides
    fun provideCoroutineScope() = CoroutineScope(Dispatchers.Main.immediate
            + SupervisorJob())
}