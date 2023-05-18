package com.bolunevdev.kinon.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.URL
import javax.inject.Inject

class DetailsFragmentViewModel : ViewModel() {

    val filmsListLiveData = MutableLiveData<List<Film>>()
    private val compositeDisposable = CompositeDisposable()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        loadFilms()
    }

    private fun loadFilms() {
        compositeDisposable.add(interactor.getFavoritesFilmsFromDB()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorComplete()
            .subscribe {
                filmsListLiveData.value = it
            })
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

    fun createAlarm(context: Context, film: Film) {
        interactor.addAlarm(context, film)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}