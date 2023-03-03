package com.bolunevdev.kinon.data

import com.bolunevdev.kinon.data.dao.FilmDao
import com.bolunevdev.kinon.data.entity.Film
import kotlinx.coroutines.flow.Flow

class MainRepository(private val filmDao: FilmDao) {
    fun putToDb(films: List<Film>) {
        filmDao.insertAll(films)
    }

    fun getAllFromDB(): Flow<List<Film>> = filmDao.getCachedFilms()

    fun deleteAllFromDB() {
        filmDao.deleteCachedFilms()
    }
}