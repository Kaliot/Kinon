package com.bolunevdev.core_api.db

import androidx.room.*
import com.bolunevdev.core_api.entity.Film
import io.reactivex.rxjava3.core.Observable

@Dao
interface FavoriteFilmDao {
    //Запрос на всю таблицу в обратном порядке
    @Query("SELECT * FROM cached_films ORDER BY id DESC")
    fun getFavoritesFilms(): Observable<List<Film>>

    //Добавляем фильм в таблицу
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteFilm(favoriteFilm: Film)

    //Удаляем фильм из таблицы
    @Query("DELETE FROM cached_films WHERE id = :id")
    fun deleteFavoriteFilm(id: Int)
}