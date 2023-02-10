package com.bolunevdev.kinon.di.modules

import android.content.Context
import androidx.room.Room
import com.bolunevdev.kinon.data.FavoriteRepository
import com.bolunevdev.kinon.data.MainRepository
import com.bolunevdev.kinon.data.dao.FavoriteFilmDao
import com.bolunevdev.kinon.data.dao.FilmDao
import com.bolunevdev.kinon.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideFilmDao(context: Context) =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "film_db"
        ).build().filmDao()

    @Provides
    @Singleton
    fun provideRepository(filmDao: FilmDao) = MainRepository(filmDao)

    @Singleton
    @Provides
    fun provideFavoriteFilmDao(context: Context) =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "favorite_film_db"
        ).build().favoriteFilmDao()

    @Provides
    @Singleton
    fun provideFavoriteRepository(favoriteFilmDao: FavoriteFilmDao) =
        FavoriteRepository(favoriteFilmDao)
}