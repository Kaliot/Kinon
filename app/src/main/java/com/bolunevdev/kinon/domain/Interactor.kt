package com.bolunevdev.kinon.domain

import com.bolunevdev.kinon.data.MainRepository

class Interactor(private val repo: MainRepository) {
    fun getFilmsDB(): List<Film> = repo.filmsDataBase
}