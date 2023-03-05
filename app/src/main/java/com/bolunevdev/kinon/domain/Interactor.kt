package com.bolunevdev.kinon.domain

import android.content.SharedPreferences
import com.bolunevdev.kinon.data.*
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.data.entity.TmdbResults
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        retrofitService.getFilms(getDefaultCategoryFromPreferences(), API.KEY, "ru-RU", page)
            .enqueue(object : Callback<TmdbResults> {
                override fun onResponse(
                    call: Call<TmdbResults>,
                    response: Response<TmdbResults>
                ) {
                    val apiList = response.body()?.results ?: emptyList()

                    Single.just(apiList).map { tmdbFilmList ->
                        val filmList = tmdbFilmList.map {
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
                        .subscribeOn(Schedulers.io())
                        .onErrorComplete()
                        .doFinally {
                            showProgressBar(false)
                        }
                        .subscribe()
                }

                override fun onFailure(call: Call<TmdbResults>, t: Throwable) {
                    showProgressBar(false)
                    showServerError(true)
                }
            })
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

    private fun showProgressBar(isShow: Boolean) {
        progressBarSubject.onNext(isShow)
    }

    private fun showServerError(isShow: Boolean) {
        serverErrorSubject.onNext(isShow)
    }
}