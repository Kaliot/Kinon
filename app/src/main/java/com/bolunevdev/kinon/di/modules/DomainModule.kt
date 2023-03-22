package com.bolunevdev.kinon.di.modules

import android.content.Context
import com.bolunevdev.core_api.db.FavoriteFilmDao
import com.bolunevdev.core_api.db.FilmDao
import com.bolunevdev.kinon.data.FavoriteRepository
import com.bolunevdev.kinon.data.MainRepository
import com.bolunevdev.kinon.data.PreferenceProvider
import com.bolunevdev.kinon.domain.Interactor
import com.bolunevdev.remote_module.TmdbApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
//Передаем контекст для SharedPreferences через конструктор
class DomainModule(val context: Context) {
    @Singleton
    @Provides
    //Создаем экземпляр SharedPreferences
    fun providePreferences(context: Context) = PreferenceProvider(context)

    @Singleton
    @Provides
    fun provideMainRepository(filmDao: FilmDao) = MainRepository(filmDao)

    @Singleton
    @Provides
    fun provideFavoriteRepository(favoriteFilmDao: FavoriteFilmDao) =
        FavoriteRepository(favoriteFilmDao)

    @Singleton
    @Provides
    fun provideInteractor(
        repository: MainRepository,
        favoriteRepository: FavoriteRepository,
        tmdbApi: TmdbApi,
        preferenceProvider: PreferenceProvider
    ) = Interactor(
        repo = repository,
        favoriteRepository = favoriteRepository,
        retrofitService = tmdbApi,
        preferences = preferenceProvider
    )
}