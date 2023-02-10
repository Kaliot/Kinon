package com.bolunevdev.kinon.data

import com.bolunevdev.kinon.data.dao.FavoriteFilmDao
import com.bolunevdev.kinon.data.entity.Film

class FavoriteRepository(private val favoriteFilmDao: FavoriteFilmDao) {
    fun getAllFavoritesFilmsFromDB(): List<Film> {
        return favoriteFilmDao.getFavoritesFilms()
    }

    fun setFilmAsFavoriteInDB(favoriteFilm: Film) {
        favoriteFilmDao.insertFavoriteFilm(favoriteFilm)
    }

    fun setFilmAsNotFavoriteInDB(id: Int) {
        favoriteFilmDao.deleteFavoriteFilm(id)
    }
}