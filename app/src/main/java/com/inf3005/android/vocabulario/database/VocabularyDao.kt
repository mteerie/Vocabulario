package com.inf3005.android.vocabulario.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(voc: Vocabulary)

    @Update
    suspend fun update(voc: Vocabulary)

    @Delete
    suspend fun delete(voc: Vocabulary)

    /**
    * Gibt die gesamte Tabelle zurück. Zur Verwendung mit RecyclerView, um alle Vokabeln anzuzeigen.
    * */
    @Query("SELECT * FROM vocabulary ORDER BY vocId")
    fun getAllEntries(): LiveData<List<Vocabulary>>

    /**
    * Löscht die gesamte Tabelle. Nicht implementiert.
    * */
    @Query("DELETE FROM vocabulary")
    suspend fun clearList()
}