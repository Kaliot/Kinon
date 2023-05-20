package com.bolunevdev.kinon.domain

import com.bolunevdev.core_api.entity.Film
import io.reactivex.rxjava3.core.Observable

interface MainRepository {

    fun putToDb(films: List<Film>)

    fun getAllFromDB(): Observable<List<Film>>

    fun deleteAllFromDB()
}