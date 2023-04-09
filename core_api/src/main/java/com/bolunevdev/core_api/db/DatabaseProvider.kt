package com.bolunevdev.core_api.db

interface DatabaseProvider {
    fun filmDao(): FilmDao
    fun favouriteFilmDao(): FavoriteFilmDao
    fun alarmDao(): AlarmDao
}