package com.inf3005.android.vocabulario.utilities

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOError
import java.io.IOException
import javax.inject.Inject

/**
 * Erzeuge DataStore zum Speichern von Nutzereinstellungen mithilfe von Dependency Injection.
 * */

class DataStorePreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {
    val dataStoreFlow = dataStore.data

        // Safety-Net für Fehler bei Zugriff durch Collector des Flows.
        .catch {
            if (it is IOError)
                emit(emptyPreferences())
        }
        // Speichere Auswahl aus PreferenceKey SORT_BY in Instanz von PreferenceProperties.
        .map {
            val sortBy = SortBy.valueOf(
                it[PreferencesKeys.SORT_BY] ?: SortBy.GERMAN.name
            )
            PreferenceProperties(sortBy)
        }

    // Von ListViewModel verwendet um Sortierung bei Input in ListFragment anpassen zu können.
    suspend fun updateSort(option: SortBy) {
        dataStore.edit {
            it[PreferencesKeys.SORT_BY] = option.name
        }
    }

    // Ein Key speichert einer wählbaren User-Preference.
    private object PreferencesKeys {
        val SORT_BY = preferencesKey<String>("sort_by")
    }
}

/**
 * Speichert Nutzerauswahl und wird von ListViewModel weiter verwendet, um Listeneinträge
 * in korrekter Sortierung anzuzeigen.
 * */
data class PreferenceProperties(val sortBy: SortBy)
