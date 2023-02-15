package com.bolunevdev.kinon.domain

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bolunevdev.kinon.data.*
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.data.entity.TmdbResults
import com.bolunevdev.kinon.utils.Converter
import com.bolunevdev.kinon.viewmodel.HomeFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Interactor(
    private val repo: MainRepository,
    private val favoriteRepository: FavoriteRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider
) {
    //В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    //и страницу, которую нужно загрузить (это для пагинации)
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        //Метод getDefaultCategoryFromPreferences() будет нам получать при каждом запросе нужный нам список фильмов
        retrofitService.getFilms(getDefaultCategoryFromPreferences(), API.KEY, "ru-RU", page)
            .enqueue(object : Callback<TmdbResults> {
                override fun onResponse(
                    call: Call<TmdbResults>,
                    response: Response<TmdbResults>
                ) {
                    //При успехе мы вызываем метод, передаем onSuccess и в этот коллбэк список фильмов
                    val list = Converter.convertApiListToDtoList(response.body()?.results)
                    //Кладём фильмы в БД
                    repo.putToDb(list)
                    callback.onSuccess()
                }

                override fun onFailure(call: Call<TmdbResults>, t: Throwable) {
                    //В случае провала вызываем другой метод коллбека
                    callback.onFailure()
                }
            })
    }

    fun tryToUpdateFilmsFromDB(callback: HomeFragmentViewModel.ApiCallback) {
        //Метод getDefaultCategoryFromPreferences() будет нам получать при каждом запросе нужный нам список фильмов
        retrofitService.getFilms(getDefaultCategoryFromPreferences(), API.KEY, "ru-RU", 1)
            .enqueue(object : Callback<TmdbResults> {
                override fun onResponse(
                    call: Call<TmdbResults>,
                    response: Response<TmdbResults>
                ) {
                    //При успехе мы вызываем метод, передаем onSuccess и в этот коллбэк список фильмов
                    val list = Converter.convertApiListToDtoList(response.body()?.results)
                    repo.updateDb(list)
                    callback.onSuccess()
                }

                override fun onFailure(call: Call<TmdbResults>, t: Throwable) {
                    //В случае провала вызываем другой метод коллбека
                    callback.onFailure()
                }
            })
    }

    fun getFilmsFromDB(): LiveData<List<Film>> = repo.getAllFromDB()

    //Метод ля очистки базы данных
    fun deleteAllFromDB() = repo.deleteAllFromDB()

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

    fun getFavoritesFilmsFromDB(): LiveData<List<Film>> =
        favoriteRepository.getAllFavoritesFilmsFromDB()

    fun setFilmAsFavoriteInDB(film: Film) = favoriteRepository.setFilmAsFavoriteInDB(film)

    //Метод ля удаления фильма из базы данных по Id
    fun setFilmAsNotFavoriteInDB(id: Int) = favoriteRepository.setFilmAsNotFavoriteInDB(id)
}