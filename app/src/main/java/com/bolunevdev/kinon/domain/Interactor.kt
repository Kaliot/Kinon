package com.bolunevdev.kinon.domain

import android.content.SharedPreferences
import com.bolunevdev.kinon.data.API
import com.bolunevdev.kinon.data.MainRepository
import com.bolunevdev.kinon.data.PreferenceProvider
import com.bolunevdev.kinon.data.TmdbApi
import com.bolunevdev.kinon.data.entity.TmdbResults
import com.bolunevdev.kinon.utils.Converter
import com.bolunevdev.kinon.viewmodel.HomeFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Interactor(
    private val repo: MainRepository,
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
                    //Кладем фильмы в бд
                    list.forEach {
                        repo.putToDb(film = it)
                    }
                    callback.onSuccess(list)
                }

                override fun onFailure(call: Call<TmdbResults>, t: Throwable) {
                    //В случае провала вызываем другой метод коллбека
                    callback.onFailure()
                }
            })
    }

    fun getFilmsFromDB(): List<Film> = repo.getAllFromDB()

    //Метод ля очистки базы данных
    fun deleteAllFromDB() = repo.deleteAllFromDB()

    //Метод ля удаления фильма из базы данных по Id
    fun deleteFilmFromDB(id: Int) = repo.deleteFilmFromDB(id)

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

    fun getFavoritesFilmsFromDB(): List<Film> = repo.getAllFavoritesFilmsFromDB()

    fun setFilmAsFavoriteInDB(id: Int) = repo.setFilmAsFavoriteInDB(id)

    fun setFilmAsNotFavoriteInDB(id: Int) = repo.setFilmAsNotFavoriteInDB(id)

    fun isFilmInFavorites(id: Int): Boolean = repo.isFilmInFavorites(id)


}