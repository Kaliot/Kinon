package com.bolunevdev.kinon.domain

import android.content.Context
import android.content.SharedPreferences
import com.bolunevdev.core_api.entity.Alarm
import com.bolunevdev.core_api.entity.Film
import io.reactivex.rxjava3.core.Observable

interface Interactor {

    fun getFilmsFromApi(page: Int)

    fun getSearchedFilms(request: String, searchPageNumber: Int): Observable<List<Film>>

    fun getFilmsFromDB(): Observable<List<Film>>

    fun deleteAllFromDB()

    fun saveDefaultCategoryToPreferences(category: String)

    fun getDefaultCategoryFromPreferences(): String

    fun addSharedPreferencesCategoryChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)

    fun unregisterSharedPreferencesCategoryChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)

    fun setTimeOfUpdate(time: Long)

    fun getTimeOfUpdate(): Long

    fun getFavoritesFilmsFromDB(): Observable<List<Film>>

    fun setFilmAsFavoriteInDB(film: Film)

    fun setFilmAsNotFavoriteInDB(id: Int)

    fun showProgressBar(isShow: Boolean)

    fun showServerError(isShow: Boolean)

    fun addAlarm(context: Context, film: Film)

    fun editAlarm(context: Context, alarm: Alarm)

    fun cancelAlarm(context: Context, alarm: Alarm)

    fun getAlarmsFromDB(): Observable<List<Alarm>>

    fun deleteOldAlarmsFromDB()

    fun observeProgressBar(): Observable<Boolean>

    fun observeServerError(): Observable<Boolean>
}