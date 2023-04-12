package com.bolunevdev.core_api.db

import androidx.room.*
import com.bolunevdev.core_api.entity.Alarm
import io.reactivex.rxjava3.core.Observable

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY timeInMillis ASC")
    fun getAll(): Observable<List<Alarm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: Alarm)

    @Delete
    fun delete(alarm: Alarm)
}