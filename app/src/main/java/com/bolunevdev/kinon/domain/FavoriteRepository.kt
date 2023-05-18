package com.bolunevdev.kinon.domain

import com.bolunevdev.core_api.entity.Film
import io.reactivex.rxjava3.core.Observable

interface FavoriteRepository {

    fun getAllFavoritesFilmsFromDB(): Observable<List<Film>>

    fun setFilmAsFavoriteInDB(favoriteFilm: Film)

    fun setFilmAsNotFavoriteInDB(id: Int)
}