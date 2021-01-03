package com.inf3005.android.vocabulario.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Hier wird die Entitiy erzeugt, d.h. der Datenbankrohling mit dem ich in der eigentlich Room-
 * Datenbank weiterarbeite.
 *
 * Es wird eine Tabelle mit drei Attributen erzeugt - einer ID (Primärschlüssel),
 * einem String für das Deutsche Wort und
 * einem String für das Spanische Wort.
 * */
@Entity(tableName = "vocabulary")
@Parcelize
data class Vocabulary(
    @ColumnInfo(name = "german")
    val de: String = "de",

    @ColumnInfo(name = "spanish")
    val sp: String = "sp",

    @PrimaryKey(autoGenerate = true)
    val vocId: Long = 0L
) : Parcelable