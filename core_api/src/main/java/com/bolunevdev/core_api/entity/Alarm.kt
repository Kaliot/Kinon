package com.bolunevdev.core_api.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "alarms", indices = [Index(value = ["film_id"], unique = true)])
@Parcelize
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "timeInMillis") val timeInMillis: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "poster_path") val poster: String?,
    @ColumnInfo(name = "overview") val description: String,
    @ColumnInfo(name = "vote_average") var rating: Double = 0.0,
    @ColumnInfo(name = "film_id") var filmId: Int
) : Parcelable {

    override fun equals(other: Any?) =
        (other is Alarm) && (filmId == other.filmId) && (timeInMillis == other.timeInMillis)

    override fun hashCode(): Int {
        var result = timeInMillis.hashCode()
        result = 31 * result + filmId
        return result
    }
}
