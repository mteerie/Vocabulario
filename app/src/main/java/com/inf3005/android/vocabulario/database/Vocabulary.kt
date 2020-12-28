package com.inf3005.android.vocabulario.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
* Hier wird die Entitiy erzeugt, d.h. der Datenbankrohling mit dem ich in der eigentlich Room-
 * Datenbank weiterarbeite.
 *
 * Es wird eine Tabelle mit drei Attributen erzeugt - einer ID (Primärschlüssel),
 * einem String für das Deutsche Wort und
 * einem String für das Spanische Wort.
 * */
@Entity(tableName = "vocabulary")
data class Vocabulary(
    @PrimaryKey(autoGenerate = true)
    val vocId: Long = 0L,

    @ColumnInfo(name = "german")
    val de: String = "de",

    @ColumnInfo(name = "spanish")
    val sp: String = "sp"
    )