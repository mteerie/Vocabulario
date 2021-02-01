package com.inf3005.android.vocabulario.utilities

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * In dieser Klasse wird ein Jetpack DataStore erzeugt, der Preferences (Nutzer-Einstellungen)
 * speichert.
 *
 * Über das Singleton-Keyword von Hilt wird angemerkt, dass lediglich eine einzige Instanz dieser
 * Klasse erzeugt werden soll - weil nie mehr als eine Instanz benötigt wird.
 *
 * über createDataStore wird ein Flow erzeugt, dessen Operatoren (hier) catch und map aufgerufen
 * werden.
 *
 * Der map-Operator wird hier zunächst verwendet, um ein val sortBy zu erzeugen. In sortBy wird
 * dann bei Aufruf der Transformationsfunktion ein Wert aus dem SortBy-enum gespeichert. Der Wert
 * entspricht dem zugehörigen Wert, der im PreferencesKey SORT_BY hinterlegt ist.
 *
 * Der Wert des PreferencesKey wird über die updateSort-Funktion angepasst, welche im ListFragment
 * bei Änderung der Sortierung mit Übergabe der zugehörigen enum-Option aus SortBy aufgerufen wird.
 * */

class DataStorePreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {
    val dataStoreFlow = dataStore.data

        /**
         * Über den catch-Operator wird eine IOException abgefangen - für den Fall, dass es
         * Probleme beim Lesen der Preferences aus dem Speicher gibt.
         * */
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
        }
        .map { preferences ->
            val sortBy = SortBy.valueOf(
                preferences[PreferencesKeys.SORT_BY] ?: SortBy.GERMAN.name
            )
            PreferenceProperties(sortBy)
        }

    private object PreferencesKeys {
        val SORT_BY = preferencesKey<String>("sort_by")
    }

    suspend fun updateSort(option: SortBy) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_BY] = option.name
        }
    }
}

/**
 * Das SortBy-enum wird verwendet um einfach die Listensortierung regeln zu können.
 * */
enum class SortBy { GERMAN, SPANISH, DIFFICULTY_ASC, DIFFICULTY_DESC }

/**
 * Diese Data Class wird erzeugt, um die im DataStoreFlow per .map-Operator erzeugten Values
 * speichern zu können. Mit den PreferenceProperties wird im ListViewModel gearbeitet, um unter
 * Berücksichtigung der vom Nutzer ausgewählten Listensortierung die zugehörige Query des DAO auf-
 * zurufen.
 * */
data class PreferenceProperties(val sortBy: SortBy)
