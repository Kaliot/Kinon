package com.bolunevdev.kinon.data

import android.content.ContentValues
import android.database.Cursor
import com.bolunevdev.kinon.data.db.DatabaseHelper
import com.bolunevdev.kinon.domain.Film

class MainRepository(databaseHelper: DatabaseHelper) {
    //Инициализируем объект для взаимодействия с БД
    private val sqlDb = databaseHelper.readableDatabase

    //Создаем курсор для обработки запросов из БД
    private lateinit var cursor: Cursor

    fun putToDb(film: Film) {
        //Создаем объект, который будет хранить пары ключ-значение, для того
        //чтобы класть нужные данные в нужные столбцы
        val cv = ContentValues()
        cv.apply {
            put(DatabaseHelper.COLUMN_TITLE, film.title)
            put(DatabaseHelper.COLUMN_POSTER, film.poster)
            put(DatabaseHelper.COLUMN_DESCRIPTION, film.description)
            put(DatabaseHelper.COLUMN_RATING, film.rating)
            put(DatabaseHelper.COLUMN_FILM_ID, film.id)
            put(DatabaseHelper.COLUMN_IS_FAVORITE, 0)
        }
        //Кладем фильм в БД
        sqlDb.insert(DatabaseHelper.TABLE_NAME, null, cv)
    }

    fun getAllFromDB(): List<Film> {
        //Создаем курсор на основании запроса "Получить все из таблицы"
        cursor = sqlDb.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME}", null)
        //Сюда будем сохранять результат получения данных
        val result = mutableListOf<Film>()
        //Проверяем, есть ли хоть одна строка в ответе на запрос
        if (cursor.moveToFirst()) {
            //Итерируемся по таблице, пока есть записи, и создаем на основании объект Film
            do {
                val title = cursor.getString(1)
                val poster = cursor.getString(2)
                val description = cursor.getString(3)
                val rating = cursor.getDouble(4)
                val filmId = cursor.getInt(5)

                result.add(Film(title, poster, description, rating, filmId))
            } while (cursor.moveToNext())
        }
        //Возвращаем список фильмов
        return result
    }

    fun deleteAllFromDB() {
        // Удаляем все фильмы из таблицы
        sqlDb.delete(DatabaseHelper.TABLE_NAME, null, null)
    }

    fun deleteFilmFromDB(filmId: Int) {
        // Удаляем фильм из таблицы по id
        sqlDb.delete(
            DatabaseHelper.TABLE_NAME, "${DatabaseHelper.COLUMN_FILM_ID} = ?",
            arrayOf(filmId.toString())
        )
    }

    fun getAllFavoritesFilmsFromDB(): List<Film> {
        //Создаем курсор на основании запроса "Получить все из таблицы"
        cursor = sqlDb.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_NAME} WHERE ${DatabaseHelper.COLUMN_IS_FAVORITE} = 1",
            null
        )
        //Сюда будем сохранять результат получения данных
        val result = mutableListOf<Film>()
        //Проверяем, есть ли хоть одна строка в ответе на запрос
        if (cursor.moveToFirst()) {
            //Итерируемся по таблице, пока есть записи, и создаем на основании объект Film
            do {
                val title = cursor.getString(1)
                val poster = cursor.getString(2)
                val description = cursor.getString(3)
                val rating = cursor.getDouble(4)
                val filmId = cursor.getInt(5)

                result.add(Film(title, poster, description, rating, filmId))
            } while (cursor.moveToNext())
        }
        //Возвращаем список фильмов
        return result
    }

    fun setFilmAsFavoriteInDB(filmId: Int) {
        val cv = ContentValues()
        cv.put(DatabaseHelper.COLUMN_IS_FAVORITE, 1)
        sqlDb.update(
            DatabaseHelper.TABLE_NAME, cv, DatabaseHelper.COLUMN_FILM_ID + "=" + "?",
            arrayOf(filmId.toString())
        )
    }

    fun setFilmAsNotFavoriteInDB(filmId: Int) {
        val cv = ContentValues()
        cv.put(DatabaseHelper.COLUMN_IS_FAVORITE, 0)
        sqlDb.update(
            DatabaseHelper.TABLE_NAME,
            cv,
            DatabaseHelper.COLUMN_FILM_ID + "=" + filmId,
            null
        )
    }

    fun isFilmInFavorites(filmId: Int): Boolean {
        cursor = sqlDb.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_NAME} WHERE ${DatabaseHelper.COLUMN_IS_FAVORITE} = 1 AND ${DatabaseHelper.COLUMN_FILM_ID} = $filmId",
            null
        )
        return cursor.count > 0
    }
}