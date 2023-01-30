package com.bolunevdev.kinon.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Film
import com.bolunevdev.kinon.domain.Interactor
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
        getFilmsFromApi()
    }

    fun getFilmsFromApi() {
        interactor.getFilmsFromApi(pageNumber, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmsList.addAll(films)
                filmsListLiveData.postValue(filmsList)
            }

            override fun onFailure() {
                filmsListLiveData.postValue(interactor.getFilmsFromDB())
            }
        })
    }

    fun addPreferenceListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        interactor.addSharedPreferencesCategoryChangeListener(listener)
    }

    fun clearFilmsList() {
        filmsList.clear()
        pageNumber = 1
    }

    fun increasePageNumber() {
        pageNumber++
    }

    fun unregisterPreferencesListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        interactor.unregisterSharedPreferencesCategoryChangeListener(listener)
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}