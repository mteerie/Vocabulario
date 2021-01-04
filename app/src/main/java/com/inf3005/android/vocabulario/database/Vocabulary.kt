package com.inf3005.android.vocabulario.database

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

/**
 * Hier wird die Entitiy erzeugt, d.h. der Datenbankrohling mit dem in der eigentlich Room-
 * Datenbank weiter gearbeitet wird.
 *
 * Es wird eine Tabelle mit drei Attributen erzeugt - einer ID (Primärschlüssel),
 * einem String für das Deutsche Wort und
 * einem String für das Spanische Wort.
 * */
@Entity(tableName = "vocabulary")
@Parcelize
@TypeConverters(DifficultyConverters::class)
data class Vocabulary(
    @ColumnInfo(name = "german")
    val de: String = "de",

    @ColumnInfo(name = "spanish")
    val sp: String = "sp",

    @ColumnInfo(name = "difficulty")
    val difficulty: Difficulty = Difficulty.EASY,

    @PrimaryKey(autoGenerate = true)
    val vocId: Long = 0L
) : Parcelable

enum class Difficulty(level: Int) {
    NONE(0),
    EASY(1),
    INTERMEDIATE(2),
    HARD(3)
}

class DifficultyConverters {
    @TypeConverter
    fun convertToDifficulty(value: Int) = enumValues<Difficulty>()[value]

    @TypeConverter
    fun convertFromDifficulty(difficulty: Difficulty) = difficulty.ordinal
}
