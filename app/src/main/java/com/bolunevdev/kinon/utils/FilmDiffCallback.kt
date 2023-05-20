package com.bolunevdev.kinon.utils

import androidx.recyclerview.widget.DiffUtil
import com.bolunevdev.core_api.entity.Film

class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {

    override fun areItemsTheSame(oldFilm: Film, newFilm: Film): Boolean {
        return oldFilm.filmId == newFilm.filmId
    }

    override fun areContentsTheSame(oldFilm: Film, newFilm: Film): Boolean {
        return oldFilm == newFilm
    }
}