package com.bolunevdev.kinon.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cached_films", indices = [Index(value = ["title"], unique = false)])
data class Film(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "poster_path") val poster: String?, //У нас будет приходить ссылка на картинку, так что теперь это String
    @ColumnInfo(name = "overview") val description: String,
    @ColumnInfo(name = "vote_average") var rating: Double = 0.0, //Приходит нецелое число с API
) : Parcelable



