package com.bolunevdev.core_api.db

interface DatabaseContract {
    fun filmDao(): FilmDao
    fun favouriteFilmDao(): FavoriteFilmDao
}