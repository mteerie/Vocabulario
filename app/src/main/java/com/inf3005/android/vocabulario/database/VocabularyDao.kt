package com.inf3005.android.vocabulario.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.inf3005.android.vocabulario.list.SortBy
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
     * Gibt die gesamte Tabelle zurück. Zur Verwendung mit RecyclerView,
     * um alle Vokabeln anzuzeigen.
     *
     * Als Parameter wird ein String übergeben, der mithilfe des Flow-Operators 'flatMapLatest'
     * verwendet wird, um die Objekte der Relation zu durchsuchen. In Verwendung mit der Such-
     * Funktion des ListFragments.
     *
     * Innerhalb des Query wird '%' verwendet, damit die Nutzereingabe auch nur teilweise dem
     * Objekt der Relation entsprechen kann - ist userQuery = 'uto' soll also trotzdem der Eintrag
     * mit 'auto' angezeigt werden.
     * */
    fun getAllEntries(userQuery: String, entryOrder: SortBy): Flow<List<Vocabulary>> =
        when(entryOrder) {
            SortBy.GERMAN -> getAllEntriesByGerman(userQuery)
            SortBy.SPANISH -> getAllEntriesBySpanish(userQuery)
        }

    @Query("SELECT * FROM vocabulary WHERE german LIKE  '%' || :userQuery || '%' OR spanish LIKE '%' || :userQuery || '%' ORDER BY german")
    fun getAllEntriesByGerman(userQuery: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE german LIKE  '%' || :userQuery || '%' OR spanish LIKE '%' || :userQuery || '%' ORDER BY spanish")
    fun getAllEntriesBySpanish(userQuery: String): Flow<List<Vocabulary>>

    /**
     * Zählt die Anzahl der Einträge in der Relation. Wird primär verwendet, um die Anzeige
     * des Alle-Einträge-Löschen-Buttons im ListFragment zu kontrollieren.
     * */
    @Query("SELECT COUNT(vocId) FROM vocabulary")
    fun countEntries(): Flow<Int>

    /**
     * Löscht alle Einträge in der Tabelle.
     * */
    @Query("DELETE FROM vocabulary")
    suspend fun clearList()

}