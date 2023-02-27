package com.bolunevdev.kinon.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.domain.Interactor
import kotlinx.coroutines.flow.Flow
import java.net.URL
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DetailsFragmentViewModel : ViewModel() {
    val filmsListFlow: Flow<List<Film>>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListFlow = interactor.getFavoritesFilmsFromDB()
    }

    fun addToFavoritesFilms(film: Film) {
        interactor.setFilmAsFavoriteInDB(film)
    }

    fun deleteFromFavoritesFilms(id: Int) {
        Executors.newSingleThreadExecutor().execute {
            interactor.setFilmAsNotFavoriteInDB(id)
        }
    }

    suspend fun loadWallpaper(url: String): Bitmap {
        return suspendCoroutine {
            val posterUrl = URL(url)
            val bitmap = BitmapFactory.decodeStream(posterUrl.openConnection().getInputStream())
            it.resume(bitmap)
        }
    }
}