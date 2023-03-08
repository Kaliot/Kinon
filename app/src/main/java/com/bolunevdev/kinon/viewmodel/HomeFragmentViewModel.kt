package com.bolunevdev.kinon.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.domain.Interactor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject


class HomeFragmentViewModel : ViewModel() {
    val filmsListObservable: Observable<List<Film>>
    val showProgressBar: BehaviorSubject<Boolean>
    val showServerError: BehaviorSubject<Boolean>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    private var pageNumber = 1

    init {
        App.instance.dagger.inject(this)
        filmsListObservable = interactor.getFilmsFromDB()
        showProgressBar = interactor.progressBarSubject
        showServerError = interactor.serverErrorSubject
        getFilms()
    }

    private fun getFilms() {
        if (isDatabaseNeedToUpdate()) {
            interactor.deleteAllFromDB()
            getFilmsFromApi()
            setNewTimeOfDatabaseUpdate()
        }
    }

    fun getFilmsFromApi() {
        interactor.getFilmsFromApi(pageNumber)
    }

    fun addPreferenceListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        interactor.addSharedPreferencesCategoryChangeListener(listener)
    }

    fun clearFilmsList() {
        interactor.deleteAllFromDB()
        pageNumber = 1
    }

    fun increasePageNumber() {
        pageNumber++
    }

    fun unregisterPreferencesListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        interactor.unregisterSharedPreferencesCategoryChangeListener(listener)
    }

    private fun setNewTimeOfDatabaseUpdate() {
        interactor.setTimeOfUpdate(System.currentTimeMillis())
    }

    private fun getTimeOfDatabaseUpdate() = interactor.getTimeOfUpdate()

    private fun isDatabaseNeedToUpdate(): Boolean {
        val lastTime = getTimeOfDatabaseUpdate()
        val currentTime = System.currentTimeMillis()
        return currentTime - lastTime >= DEFAULT_TIME
    }

    companion object {
        const val DEFAULT_TIME = 600_000L
    }
}