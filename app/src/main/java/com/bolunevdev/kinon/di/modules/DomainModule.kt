package com.bolunevdev.kinon.di.modules

import android.content.Context
import com.bolunevdev.kinon.data.PreferenceProvider
import com.bolunevdev.kinon.notifications.WatchLaterNotificationHelper
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
    fun provideWatchLaterNotificationHelper() =
        WatchLaterNotificationHelper
}