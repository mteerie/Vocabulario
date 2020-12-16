package com.inf3005.android.vocabulario.database

import androidx.lifecycle.LiveData
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
    @Query("SELECT * FROM vocabulary ORDER BY german")
    fun getAllEntries(): Flow<List<Vocabulary>>

    @Query("SELECT COUNT(id) FROM vocabulary")
    fun countEntries() : Flow<Int>

    /**
    * Löscht alle Einträge in der Tabelle.
    * */
    @Query("DELETE FROM vocabulary")
    suspend fun clearList()

}