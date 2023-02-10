package com.bolunevdev.kinon.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bolunevdev.kinon.data.dao.FavoriteFilmDao
import com.bolunevdev.kinon.data.dao.FilmDao
import com.bolunevdev.kinon.data.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
    abstract fun favoriteFilmDao(): FavoriteFilmDao
}