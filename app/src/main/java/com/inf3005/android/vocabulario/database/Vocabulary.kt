package com.inf3005.android.vocabulario.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class Vocabulary(
    @PrimaryKey(autoGenerate = true)
    var vocId: Long = 0L,

    @ColumnInfo(name = "deutsch")
    val de: String = "de",

    @ColumnInfo(name = "spanisch")
    val sp: String = "sp"
)