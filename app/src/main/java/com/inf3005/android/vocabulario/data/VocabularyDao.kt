package com.inf3005.android.vocabulario.data

import androidx.room.*
import com.inf3005.android.vocabulario.utilities.SortBy
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Vocabulary)

    @Update
    suspend fun update(entry: Vocabulary)

    @Delete
    suspend fun delete(entry: Vocabulary)

    /**
     * Gibt die gesamte Tabelle zurück. Zur Verwendung mit RecyclerView,
     * um alle Vokabeln anzuzeigen.
     *
     * Als Parameter wird ein String übergeben, der mithilfe des Flow-Operators 'flatMapLatest'
     * verwendet wird, um die Objekte der Relation zu durchsuchen. In Verwendung mit der Such-
     * Funktion des ListFragments.
     *
     * Innerhalb der Queries wird '%' als Platzhalter verwendet, sodass die Nutzereingabe auch nur teilweise dem
     * Objekt der Relation entsprechen kann - ist userQuery == 'uto' soll also trotzdem ein Eintrag
     * mit Wert 'auto' in einem der Attribute angezeigt werden.
     * */
    fun getAllEntries(
        userQuery: String,
        entryOrder: SortBy
    ): Flow<List<Vocabulary>> =
        when (entryOrder) {
            SortBy.GERMAN -> getAllEntriesByGerman(userQuery)
            SortBy.SPANISH -> getAllEntriesBySpanish(userQuery)
            SortBy.DIFFICULTY_ASC -> getAllEntriesByDifficultyAscending(userQuery)
            SortBy.DIFFICULTY_DESC -> getAllEntriesByDifficultyDescending(userQuery)
        }

    @Query("SELECT * FROM vocabulary WHERE (binned = 0) AND german LIKE '%' || :userQuery || '%' OR (binned = 0) AND spanish LIKE '%' || :userQuery || '%' ORDER BY UPPER(german)")
    fun getAllEntriesByGerman(userQuery: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE (binned = 0) AND german LIKE '%' || :userQuery || '%' OR (binned = 0) AND spanish LIKE '%' || :userQuery || '%' ORDER BY UPPER(spanish)")
    fun getAllEntriesBySpanish(userQuery: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE (binned = 0) AND german LIKE '%' || :userQuery || '%' OR (binned = 0) AND spanish LIKE '%' || :userQuery || '%' ORDER BY difficulty ASC, UPPER(german)")
    fun getAllEntriesByDifficultyAscending(userQuery: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE (binned = 0) AND german LIKE '%' || :userQuery || '%' OR (binned = 0) AND spanish LIKE '%' || :userQuery || '%' ORDER BY difficulty DESC, UPPER(german)")
    fun getAllEntriesByDifficultyDescending(userQuery: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE (binned = 1) AND german LIKE '%' || :userQuery || '%' OR (binned = 1) AND spanish LIKE '%' || :userQuery || '%' ORDER BY UPPER(german)")
    fun getAllBinnedEntries(userQuery: String): Flow<List<Vocabulary>>

    /**
     * Zählt die Anzahl der Einträge in der Relation. Wird primär verwendet, um die Anzeige
     * des Alle-Einträge-Löschen-Buttons im ListFragment zu kontrollieren.
     * */
    @Query("SELECT COUNT(vocId) FROM vocabulary WHERE binned = 0")
    fun countEntries(): Flow<Int>

    @Query("SELECT COUNT(vocId) FROM vocabulary WHERE binned = 1")
    fun countBinnedEntries(): Flow<Int>

    /**
     * Löscht alle Einträge in der Tabelle. Derzeit nur in Verwendung mit dem Database-Callback
     * in VocabularyDatabase.kt, um bei Erstellung der Datenbank zur Sicherheit etwaige bereits
     * vorhandene Einträge zu löschen.
     * */
    @Query("DELETE FROM vocabulary")
    suspend fun clearList()

    @Query("DELETE FROM vocabulary WHERE binned = 1")
    suspend fun clearBin()
}