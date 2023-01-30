package com.bolunevdev.kinon.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Film
import com.bolunevdev.kinon.domain.Interactor
import javax.inject.Inject

class FavoritesFragmentViewModel : ViewModel() {
    val filmsListLiveData: MutableLiveData<List<Film>> = MutableLiveData()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        getFilmsFromDB()
    }

    fun getFilmsFromDB() {
        filmsListLiveData.postValue(interactor.getFavoritesFilmsFromDB())
    }
}