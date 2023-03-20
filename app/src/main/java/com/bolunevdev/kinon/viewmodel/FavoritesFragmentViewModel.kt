package com.bolunevdev.kinon.viewmodel

import androidx.lifecycle.ViewModel
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Interactor
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class FavoritesFragmentViewModel : ViewModel() {
    val filmsListObservable: Observable<List<Film>>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListObservable = interactor.getFavoritesFilmsFromDB()
    }
}