package com.bolunevdev.kinon.utils

import androidx.recyclerview.widget.DiffUtil
import com.bolunevdev.kinon.data.entity.Film

class FilmDiff(private val oldList: List<Film>, private val newList: List<Film>) :
    DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].filmId == newList[newItemPosition].filmId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFilm = oldList[oldItemPosition]
        val newFilm = newList[newItemPosition]
        return oldFilm == newFilm
    }
}