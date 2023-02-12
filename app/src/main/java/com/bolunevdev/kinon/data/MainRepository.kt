package com.bolunevdev.kinon.data

import androidx.lifecycle.LiveData
import com.bolunevdev.kinon.data.dao.FilmDao
import com.bolunevdev.kinon.data.entity.Film
import java.util.concurrent.Executors

class MainRepository(private val filmDao: FilmDao) {
    fun putToDb(films: List<Film>) {
        //Запросы в БД должны быть в отдельном потоке
        Executors.newSingleThreadExecutor().execute {
            filmDao.insertAll(films)
        }
    }

    fun getAllFromDB(): LiveData<List<Film>> = filmDao.getCachedFilms()

    fun updateDb(films: List<Film>) {
        Executors.newSingleThreadExecutor().execute {
            filmDao.deleteCachedFilms()
            filmDao.insertAll(films)
        }
    }

    fun deleteAllFromDB() {
        Executors.newSingleThreadExecutor().execute {
            filmDao.deleteCachedFilms()
        }
    }
}