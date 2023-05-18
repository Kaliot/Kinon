package com.bolunevdev.kinon.di.modules

import com.bolunevdev.kinon.data.AlarmRepositoryImpl
import com.bolunevdev.kinon.data.FavoriteRepositoryImpl
import com.bolunevdev.kinon.data.MainRepositoryImpl
import com.bolunevdev.kinon.domain.AlarmRepository
import com.bolunevdev.kinon.domain.FavoriteRepository
import com.bolunevdev.kinon.domain.MainRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindMainRepository(mainRepositoryImpl: MainRepositoryImpl): MainRepository

    @Singleton
    @Binds
    abstract fun bindAlarmRepository(alarmRepositoryImpl: AlarmRepositoryImpl): AlarmRepository

    @Singleton
    @Binds
    abstract fun bindFavoriteRepository(favoriteRepositoryImpl: FavoriteRepositoryImpl): FavoriteRepository
}