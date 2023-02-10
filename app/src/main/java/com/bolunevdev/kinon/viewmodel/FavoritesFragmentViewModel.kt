package com.bolunevdev.kinon.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.domain.Interactor
import java.util.concurrent.Executors
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
        Executors.newSingleThreadExecutor().execute {
            filmsListLiveData.postValue(interactor.getFavoritesFilmsFromDB())
        }
    }
}