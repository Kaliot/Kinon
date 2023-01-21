package com.bolunevdev.kinon.di.modules

import com.bolunevdev.kinon.data.MainRepository
import com.bolunevdev.kinon.data.TmdbApi
import com.bolunevdev.kinon.domain.Interactor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {
    @Singleton
    @Provides
    fun provideInteractor(repository: MainRepository, tmdbApi: TmdbApi) =
        Interactor(repo = repository, retrofitService = tmdbApi)
}