package com.bolunevdev.kinon.data

import com.bolunevdev.kinon.data.dao.FilmDao
import com.bolunevdev.kinon.data.entity.Film
import io.reactivex.rxjava3.core.Observable

class MainRepository(private val filmDao: FilmDao) {
    fun putToDb(films: List<Film>) {
        filmDao.insertAll(films)
    }

    fun getAllFromDB(): Observable<List<Film>> = filmDao.getCachedFilms()

    fun deleteAllFromDB() {
        filmDao.deleteCachedFilms()
    }
}