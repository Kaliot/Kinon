package com.bolunevdev.kinon.domain

import android.content.SharedPreferences
import com.bolunevdev.kinon.data.*
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.data.entity.TmdbFilm
import com.bolunevdev.kinon.data.entity.TmdbResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Interactor(
    private val repo: MainRepository,
    private val favoriteRepository: FavoriteRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    val progressBarChannel = Channel<Boolean>(Channel.CONFLATED)
    val serverErrorChannel = Channel<Boolean>(Channel.CONFLATED)

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
                    if (apiList.isNotEmpty()) {
                        scope.launch {
                            //Кладём фильмы в БД
                            mapTmdbFilmsToFilmsWithFlow(apiList).collect {
                                repo.putToDb(it)
                            }
                            progressBarChannel.send(false)
                        }
                        showServerError(false)
                    } else {
                        showProgressBar(false)
                        showServerError(true)
                    }
                }

                override fun onFailure(call: Call<TmdbResults>, t: Throwable) {
                    showProgressBar(false)
                    showServerError(true)
                }
            })
    }

    private fun mapTmdbFilmsToFilmsWithFlow(list: List<TmdbFilm>): Flow<List<Film>> {
        return flow {
            emit(list.map {
                Film(
                    title = it.title,
                    poster = it.posterPath,
                    description = it.overview,
                    rating = it.voteAverage,
                    filmId = it.id
                )
            })
        }
    }

    fun getFilmsFromDB(): Flow<List<Film>> = repo.getAllFromDB()

    //Метод ля очистки базы данных
    fun deleteAllFromDB() {
        scope.launch {
            repo.deleteAllFromDB()
        }
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

    fun getFavoritesFilmsFromDB(): Flow<List<Film>> =
        favoriteRepository.getAllFavoritesFilmsFromDB()

    fun setFilmAsFavoriteInDB(film: Film) = favoriteRepository.setFilmAsFavoriteInDB(film)

    //Метод ля удаления фильма из базы данных по Id
    fun setFilmAsNotFavoriteInDB(id: Int) = favoriteRepository.setFilmAsNotFavoriteInDB(id)

    private fun showProgressBar(isShow: Boolean) {
        scope.launch {
            progressBarChannel.send(isShow)
        }
    }

    private fun showServerError(isShow: Boolean) {
        scope.launch {
            serverErrorChannel.send(isShow)
        }
    }
}