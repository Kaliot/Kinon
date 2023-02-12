package com.bolunevdev.kinon.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bolunevdev.kinon.data.entity.Film

@Dao
interface FilmDao {
    //Запрос на всю таблицу
    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): LiveData<List<Film>>

    //Кладём списком в БД, в случае конфликта перезаписываем
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    //Удаляем все записи из таблицы
    @Query("DELETE FROM cached_films")
    fun deleteCachedFilms()
}