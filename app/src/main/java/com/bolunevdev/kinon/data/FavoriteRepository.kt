package com.bolunevdev.kinon.data

import com.bolunevdev.core_api.db.FavoriteFilmDao
import com.bolunevdev.core_api.entity.Film
import io.reactivex.rxjava3.core.Observable

class FavoriteRepository(private val favoriteFilmDao: FavoriteFilmDao) {
    fun getAllFavoritesFilmsFromDB(): Observable<List<Film>> = favoriteFilmDao.getFavoritesFilms()

    fun setFilmAsFavoriteInDB(favoriteFilm: Film) {
        favoriteFilmDao.insertFavoriteFilm(favoriteFilm)
    }

    fun setFilmAsNotFavoriteInDB(id: Int) {
        favoriteFilmDao.deleteFavoriteFilm(id)
    }
}