package com.bolunevdev.kinon.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bolunevdev.kinon.data.entity.Film

@Dao
interface FavoriteFilmDao {
    //Запрос на всю таблицу в обратном порядке
    @Query("SELECT * FROM cached_films ORDER BY id DESC")
    fun getFavoritesFilms(): LiveData<List<Film>>

    //Добавляем фильм в таблицу
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteFilm(favoriteFilm: Film)

    //Удаляем фильм из таблицы
    @Query("DELETE FROM cached_films WHERE id = :id")
    fun deleteFavoriteFilm(id: Int)
}