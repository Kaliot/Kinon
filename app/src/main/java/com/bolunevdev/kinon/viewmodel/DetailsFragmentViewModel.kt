package com.bolunevdev.kinon.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Interactor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.net.URL
import javax.inject.Inject

class DetailsFragmentViewModel : ViewModel() {
    val filmsListObservable: Observable<List<Film>>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListObservable = interactor.getFavoritesFilmsFromDB()
    }

    fun addToFavoritesFilms(film: Film) {
        interactor.setFilmAsFavoriteInDB(film)
    }

    fun deleteFromFavoritesFilms(id: Int) {
        interactor.setFilmAsNotFavoriteInDB(id)
    }

    fun loadWallpaper(url: String): Single<Bitmap> {
        return Single.fromCallable {
            val posterUrl = URL(url)
            BitmapFactory.decodeStream(posterUrl.openConnection().getInputStream())
        }
    }
}