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
     * Funktion, die abhängig von userQuery und entryOrder die passende Datenbankanfragefunktion
     * aufruft.
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

    /**
     * userQuery dient als Platzhalter für die Suchfeldeingabe des Nutzers.
     *
     * Geben Flows aus, die Listen von Vocabulary-Objekten enthalten. Erlaubt einfaches Reagieren
     * auf Änderungen an Datenbankeinträgen.
     *
     * '%' dient als Platzhalter, damit userQuery auch nur einen Teil der Werte abdecken kann.
     * Beispiel: Eingabe 'uto' soll trotzdem Eintrag mit 'Auto' anzeigen können.
     * */
    @Query("SELECT * FROM vocabulary WHERE (binned = 0) AND german LIKE '%' || :userQuery || '%' OR (binned = 0) AND spanish LIKE '%' || :userQuery || '%' ORDER BY UPPER(german)")
    fun getAllEntriesByGerman(userQuery: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE (binned = 0) AND german LIKE '%' || :userQuery || '%' OR (binned = 0) AND spanish LIKE '%' || :userQuery || '%' ORDER BY UPPER(spanish)")
    fun getAllEntriesBySpanish(userQuery: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE (binned = 0) AND german LIKE '%' || :userQuery || '%' OR (binned = 0) AND spanish LIKE '%' || :userQuery || '%' ORDER BY difficulty ASC, UPPER(german)")
    fun getAllEntriesByDifficultyAscending(userQuery: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE (binned = 0) AND german LIKE '%' || :userQuery || '%' OR (binned = 0) AND spanish LIKE '%' || :userQuery || '%' ORDER BY difficulty DESC, UPPER(german)")
    fun getAllEntriesByDifficultyDescending(userQuery: String): Flow<List<Vocabulary>>

    /**
     * Datenbankanfragefunktion, die für die Darstellung der im Papierkorb befindlichen Einträge
     * verwendet wird.
     * */
    @Query("SELECT * FROM vocabulary WHERE (binned = 1) AND german LIKE '%' || :userQuery || '%' OR (binned = 1) AND spanish LIKE '%' || :userQuery || '%' ORDER BY UPPER(german)")
    fun getAllBinnedEntries(userQuery: String): Flow<List<Vocabulary>>

    /**
     * Datenbankanfragefunktionen, die verwendet werden, um respektive alle Einträge zu zählen, die
     * sich entweder im Papierkorb befinden, oder nicht.
     * */
    @Query("SELECT COUNT(vocId) FROM vocabulary WHERE binned = 0")
    fun countEntries(): Flow<Int>

    @Query("SELECT COUNT(vocId) FROM vocabulary WHERE binned = 1")
    fun countBinnedEntries(): Flow<Int>

    /**
     * Datenbankanfragefunktionen, die verwendet werden, um respektive ALLE Einträge in der
     * Datenbank zu löschen, oder nur jene, die sich im Papierkorb befinden.
     * */
    @Query("DELETE FROM vocabulary")
    suspend fun clearList()

    @Query("DELETE FROM vocabulary WHERE binned = 1")
    suspend fun clearBin()
}