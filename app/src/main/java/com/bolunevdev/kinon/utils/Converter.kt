package com.bolunevdev.kinon.utils

import com.bolunevdev.kinon.data.entity.TmdbFilm
import com.bolunevdev.kinon.domain.Film

object Converter {
    fun convertApiListToDtoList(list: List<TmdbFilm>?): List<Film> {
        val result = mutableListOf<Film>()
        list?.forEach {
            result.add(Film(
                title = it.title,
                poster = it.posterPath,
                description = it.overview,
                rating = it.voteAverage,
                id = it.id
            ))
        }
        return result
    }
}