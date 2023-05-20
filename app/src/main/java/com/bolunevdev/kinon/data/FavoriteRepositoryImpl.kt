package com.bolunevdev.kinon.data

import com.bolunevdev.core_api.db.FavoriteFilmDao
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.domain.FavoriteRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(private val favoriteFilmDao: FavoriteFilmDao) :
    FavoriteRepository {

    override fun getAllFavoritesFilmsFromDB(): Observable<List<Film>> =
        favoriteFilmDao.getFavoritesFilms()

    override fun setFilmAsFavoriteInDB(favoriteFilm: Film) {
        favoriteFilmDao.insertFavoriteFilm(favoriteFilm)
    }

    override fun setFilmAsNotFavoriteInDB(id: Int) {
        favoriteFilmDao.deleteFavoriteFilm(id)
    }
}