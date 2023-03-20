package com.bolunevdev.kinon.domain

import android.content.SharedPreferences
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.data.FavoriteRepository
import com.bolunevdev.kinon.data.MainRepository
import com.bolunevdev.kinon.data.PreferenceProvider
import com.bolunevdev.remote_module.API
import com.bolunevdev.remote_module.TmdbApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class Interactor(
    private val repo: MainRepository,
    private val favoriteRepository: FavoriteRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider
) {
    val progressBarSubject = BehaviorSubject.create<Boolean>()
    val serverErrorSubject = BehaviorSubject.create<Boolean>()

    //В конструктор мы будем передавать страницу, которую нужно загрузить (это для пагинации)
    fun getFilmsFromApi(page: Int) {
        showProgressBar(true)
        showServerError(false)
        //Метод getDefaultCategoryFromPreferences() будет нам получать при каждом запросе нужный нам список фильмов
        retrofitService.getFilms(
            getDefaultCategoryFromPreferences(),
            API.KEY,
            "ru-RU",
            page
        )
            .subscribeOn(Schedulers.io())
            .map { tmdbFilmList ->
                val filmList = tmdbFilmList.results.map {
                    Film(
                        title = it.title,
                        poster = it.posterPath,
                        description = it.overview,
                        rating = it.voteAverage,
                        filmId = it.id
                    )
                }
                repo.putToDb(filmList)
            }
            .doOnError {
                showProgressBar(false)
                showServerError(true)
            }
            .onErrorComplete()
            .doFinally {
                showProgressBar(false)
            }
            .subscribe()
    }

    fun getSearchedFilms(request: String, searchPageNumber: Int): Observable<List<Film>> {
        return retrofitService.getSearchedFilms(
            API.KEY,
            "ru-RU",
            request,
            searchPageNumber
        )
            .map { TmdbResults ->
                TmdbResults.results.map {
                    Film(
                        title = it.title,
                        poster = it.posterPath,
                        description = it.overview,
                        rating = it.voteAverage,
                        filmId = it.id
                    )
                }
            }
    }

    fun getFilmsFromDB(): Observable<List<Film>> = repo.getAllFromDB()

    //Метод ля очистки базы данных
    fun deleteAllFromDB() {
        Completable.fromAction {
            repo.deleteAllFromDB()
        }
            .subscribeOn(Schedulers.io())
            .onErrorComplete()
            .subscribe()
    }

    //Метод для сохранения настроек
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    //Метод для получения настроек
    fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()

    //Метод для добавления слушателя категорий из настроек
    fun addSharedPreferencesCategoryChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.addSharedPreferencesCategoryChangeListener(listener)
    }

    //Метод для удаления слушателя категорий из настроек
    fun unregisterSharedPreferencesCategoryChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterSharedPreferencesCategoryChangeListener(listener)
    }

    fun setTimeOfUpdate(time: Long) = preferences.setTimeOfDatabaseUpdate(time)

    fun getTimeOfUpdate(): Long = preferences.getTimeOfDatabaseUpdate()

    fun getFavoritesFilmsFromDB(): Observable<List<Film>> =
        favoriteRepository.getAllFavoritesFilmsFromDB()

    fun setFilmAsFavoriteInDB(film: Film) = favoriteRepository.setFilmAsFavoriteInDB(film)

    //Метод ля удаления фильма из базы данных по Id
    fun setFilmAsNotFavoriteInDB(id: Int) = favoriteRepository.setFilmAsNotFavoriteInDB(id)

    fun showProgressBar(isShow: Boolean) {
        progressBarSubject.onNext(isShow)
    }

    fun showServerError(isShow: Boolean) {
        serverErrorSubject.onNext(isShow)
    }
}