package com.bolunevdev.kinon.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Film
import com.bolunevdev.kinon.domain.Interactor
import javax.inject.Inject

class DetailsFragmentViewModel : ViewModel() {
    val filmsListLiveData: MutableLiveData<List<Film>> = MutableLiveData()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        getFavoritesFilms()
    }

    private fun getFavoritesFilms() {
        filmsListLiveData.postValue(interactor.getFavoritesFilmsFromDB())
    }

    fun isFilmInFavorites(id: Int): Boolean = interactor.isFilmInFavorites(id)

    fun addToFavoritesFilms(id: Int) {
        interactor.setFilmAsFavoriteInDB(id)
        getFavoritesFilms()
    }

    fun deleteFromFavoritesFilms(id: Int) {
        interactor.setFilmAsNotFavoriteInDB(id)
        getFavoritesFilms()
    }
}