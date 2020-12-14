package com.inf3005.android.vocabulario.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: Vocabulary)

    @Update
    suspend fun update(entry: Vocabulary)

    @Delete
    suspend fun delete(entry: Vocabulary)

    /**
    * Gibt die gesamte Tabelle zurück. Zur Verwendung mit RecyclerView, um alle Vokabeln anzuzeigen.
    * */
    @Query("SELECT * FROM vocabulary ORDER BY id")
    fun getAllEntries(): Flow<List<Vocabulary>>

    /**
    * Löscht alle Einträge in der Tabelle.
    * */
    @Query("DELETE FROM vocabulary")
    suspend fun clearList()
}