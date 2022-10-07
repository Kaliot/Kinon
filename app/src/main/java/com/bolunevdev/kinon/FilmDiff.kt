package com.bolunevdev.kinon

import androidx.recyclerview.widget.DiffUtil

class FilmDiff(private val oldList: MutableList<Film>, private val newList: MutableList<Film>) :
    DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].poster == newList[newItemPosition].poster
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFilm = oldList[oldItemPosition]
        val newFilm = newList[newItemPosition]
        return oldFilm == newFilm
    }
}