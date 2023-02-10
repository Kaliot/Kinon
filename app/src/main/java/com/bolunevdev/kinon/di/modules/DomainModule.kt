package com.bolunevdev.kinon.di.modules

import android.content.Context
import com.bolunevdev.kinon.data.FavoriteRepository
import com.bolunevdev.kinon.data.MainRepository
import com.bolunevdev.kinon.data.PreferenceProvider
import com.bolunevdev.kinon.data.TmdbApi
import com.bolunevdev.kinon.domain.Interactor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
//Передаем контекст для SharedPreferences через конструктор
class DomainModule(val context: Context) {
    //Нам нужно контекст как-то провайдить, поэтому создаем такой метод
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    //Создаем экземпляр SharedPreferences
    fun providePreferences(context: Context) = PreferenceProvider(context)

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