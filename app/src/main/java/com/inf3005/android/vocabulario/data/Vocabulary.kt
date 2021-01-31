package com.inf3005.android.vocabulario.data

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
    val de: String,

    @ColumnInfo(name = "spanish")
    val sp: String,

    @ColumnInfo(name = "difficulty")
    val difficulty: Difficulty = Difficulty.EASY,

    val binned: Boolean = false,

    @PrimaryKey(autoGenerate = true)
    val vocId: Int = 0
) : Parcelable

enum class Difficulty {
    NONE,
    EASY,
    INTERMEDIATE,
    HARD
}

class DifficultyConverters {
    @TypeConverter
    fun convertToDifficulty(value: Int) = enumValues<Difficulty>()[value]

    @TypeConverter
    fun convertFromDifficulty(difficulty: Difficulty) = difficulty.ordinal
}
