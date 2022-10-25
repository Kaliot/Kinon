package com.bolunevdev.kinon

import android.content.Context

class FavoriteFilms(context: Context, filmsDataBase: MutableList<Film>) {

    private var films = mutableListOf<Film>()
    private val preferences =
        context.getSharedPreferences(MainActivity.FAVORITE_FILMS_PREFERENCES, Context.MODE_PRIVATE)
    private lateinit var idString: String

    init {
        val idString = preferences.getString(PREF_FF_KEY, null)

        if (idString != null) {
            val idList = idString.split(SEPARATOR)
            try {
                idList.forEach { this.addFilmById(it, filmsDataBase) }
            } catch (_: NumberFormatException) {
                preferences.edit().putString(PREF_FF_KEY, "").apply()
            }
        }
    }

    fun deleteFilm(film: Film) {
        val indexToDelete = films.indexOfFirst { it.id == film.id }
        if (indexToDelete != -1) {
            films.removeAt(indexToDelete)
            idString = ""
            films.forEach { idString += it.id.toString() + SEPARATOR }
            idString = idString.dropLast(NUM_TO_DROP)
            preferences.edit().putString(PREF_FF_KEY, idString).apply()
        }
    }

    fun addFilm(film: Film) {
        films.add(INDEX_TO_ADD, film)
        idString = ""
        films.forEach { idString += it.id.toString() + SEPARATOR }
        idString = idString.dropLast(2)
        preferences.edit().putString(PREF_FF_KEY, idString).apply()
    }

    private fun addFilmById(id: String, filmsDataBase: MutableList<Film>?) {
        if (filmsDataBase != null && id.isNotEmpty()) {
            filmsDataBase.find { it.id == id.toInt() }?.let { films.add(it) }
        }
    }

    fun getFilms(): MutableList<Film> = films

    fun contains(film: Film): Boolean = films.indexOfFirst { it.id == film.id } != -1


    companion object {
        private const val PREF_FF_KEY = "PREF_FF_KEY"
        private const val INDEX_TO_ADD = 0
        private const val SEPARATOR = "||"
        private const val NUM_TO_DROP = 2
    }
}