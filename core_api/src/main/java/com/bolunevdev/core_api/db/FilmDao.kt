package com.bolunevdev.core_api.db

import androidx.room.*
import com.bolunevdev.core_api.entity.Film
import io.reactivex.rxjava3.core.Observable

@Dao
interface FilmDao {
    //Запрос на всю таблицу
    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): Observable<List<Film>>

    //Кладём списком в БД, в случае конфликта перезаписываем
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    //Удаляем все записи из таблицы
    @Query("DELETE FROM cached_films")
    fun deleteCachedFilms()
}