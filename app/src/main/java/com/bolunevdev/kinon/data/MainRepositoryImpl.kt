package com.bolunevdev.kinon.data

import com.bolunevdev.core_api.db.FilmDao
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.domain.MainRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(private val filmDao: FilmDao) : MainRepository {
    override fun putToDb(films: List<Film>) {
        filmDao.insertAll(films)
    }

    override fun getAllFromDB(): Observable<List<Film>> = filmDao.getCachedFilms()

    override fun deleteAllFromDB() {
        filmDao.deleteCachedFilms()
    }
}