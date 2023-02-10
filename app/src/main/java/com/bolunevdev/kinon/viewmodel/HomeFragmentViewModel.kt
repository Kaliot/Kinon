package com.bolunevdev.kinon.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.domain.Interactor
import java.util.concurrent.Executors
import javax.inject.Inject


class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData: MutableLiveData<List<Film>> = MutableLiveData()
    private val filmsList = mutableListOf<Film>()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    private var pageNumber = 1

    init {
        App.instance.dagger.inject(this)
        getFilms()
    }

    private fun getFilms() {
        if (isDatabaseNeedToUpdate()) {
            interactor.tryToUpdateFilmsFromDB(object : ApiCallback {
                override fun onSuccess(films: List<Film>) {
                    filmsList.addAll(films)
                    filmsListLiveData.postValue(filmsList)
                    setNewTimeOfDatabaseUpdate()
                }

                override fun onFailure() {
                    loadFilmsFromDB()
                }
            })
        } else {
            loadFilmsFromDB()
        }
    }

    fun getFilmsFromApi() {
        interactor.getFilmsFromApi(pageNumber, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmsList.addAll(films)
                filmsListLiveData.postValue(filmsList)
            }

            override fun onFailure() {
                loadFilmsFromDB()
            }
        })
    }

    fun addPreferenceListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        interactor.addSharedPreferencesCategoryChangeListener(listener)
    }

    fun clearFilmsList() {
        filmsList.clear()
        interactor.deleteAllFromDB()
        pageNumber = 1
    }

    fun increasePageNumber() {
        pageNumber++
    }

    fun unregisterPreferencesListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        interactor.unregisterSharedPreferencesCategoryChangeListener(listener)
    }

    private fun loadFilmsFromDB() {
        Executors.newSingleThreadExecutor().execute {
            filmsList.addAll(interactor.getFilmsFromDB())
            filmsListLiveData.postValue(filmsList)
        }
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

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }

    companion object {
        const val DEFAULT_TIME = 600_000L
    }
}