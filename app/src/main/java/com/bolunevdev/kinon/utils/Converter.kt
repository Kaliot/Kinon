package com.bolunevdev.kinon.utils

import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.data.entity.TmdbFilm

object Converter {
    fun convertApiListToDtoList(list: List<TmdbFilm>?): List<Film> {
        val result = mutableListOf<Film>()
        list?.forEach {
            result.add(
                Film(
                    id = it.id,
                    title = it.title,
                    poster = it.posterPath,
                    description = it.overview,
                    rating = it.voteAverage,
                )
            )
        }
        return result
    }
}