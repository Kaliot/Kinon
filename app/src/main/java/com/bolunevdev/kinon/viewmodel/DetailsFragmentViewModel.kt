package com.bolunevdev.kinon.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.domain.Interactor
import java.util.concurrent.Executors
import javax.inject.Inject

class DetailsFragmentViewModel : ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        getFavoritesFilms()
    }

    private fun getFavoritesFilms() {
        Executors.newSingleThreadExecutor().execute {
            filmsListLiveData.postValue(interactor.getFavoritesFilmsFromDB())
        }
    }

    fun addToFavoritesFilms(film: Film) {
        Executors.newSingleThreadExecutor().execute {
            interactor.setFilmAsFavoriteInDB(film)
            getFavoritesFilms()
        }
    }

    fun deleteFromFavoritesFilms(id: Int) {
        Executors.newSingleThreadExecutor().execute {
            interactor.setFilmAsNotFavoriteInDB(id)
            getFavoritesFilms()
        }
    }
}