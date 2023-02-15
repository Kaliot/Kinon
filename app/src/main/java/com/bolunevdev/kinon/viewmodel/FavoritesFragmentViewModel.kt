package com.bolunevdev.kinon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.domain.Interactor
import javax.inject.Inject

class FavoritesFragmentViewModel : ViewModel() {
    val filmsListLiveData: LiveData<List<Film>>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFavoritesFilmsFromDB()
    }
}