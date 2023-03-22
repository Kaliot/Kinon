package com.bolunevdev.core_impl

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bolunevdev.core_api.db.DatabaseContract
import com.bolunevdev.core_api.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase(), DatabaseContract