package com.bolunevdev.kinon.viewmodel

import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.domain.Interactor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesFragmentViewModel : ViewModel() {
    val filmsListFlow: Flow<List<Film>>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListFlow = interactor.getFavoritesFilmsFromDB()
    }
}