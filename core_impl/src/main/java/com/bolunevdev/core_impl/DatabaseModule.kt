package com.bolunevdev.core_impl

import android.content.Context
import androidx.room.Room
import com.bolunevdev.core_api.db.AlarmDao
import com.bolunevdev.core_api.db.DatabaseContract
import com.bolunevdev.core_api.db.FavoriteFilmDao
import com.bolunevdev.core_api.db.FilmDao
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    @Named(FILM_DB_NAME)
    fun provideFilmDB(context: Context): DatabaseContract {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            FILM_DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFilmDao(@Named(FILM_DB_NAME) databaseContract: DatabaseContract): FilmDao {
        return databaseContract.filmDao()
    }

    @Singleton
    @Provides
    @Named(FAVORITE_FILM_DB_NAME)
    fun provideFavoriteFilmDB(context: Context): DatabaseContract {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            FAVORITE_FILM_DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFavoriteFilmDao(@Named(FAVORITE_FILM_DB_NAME) databaseContract: DatabaseContract): FavoriteFilmDao {
        return databaseContract.favouriteFilmDao()
    }

    @Singleton
    @Provides
    @Named(ALARM_DB_NAME)
    fun provideAlarmDB(context: Context): DatabaseContract {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            ALARM_DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideAlarmDao(@Named(ALARM_DB_NAME) databaseContract: DatabaseContract): AlarmDao {
        return databaseContract.alarmDao()
    }

    companion object {
        private const val FILM_DB_NAME = "film_db"
        private const val FAVORITE_FILM_DB_NAME = "favorite_film_db"
        private const val ALARM_DB_NAME = "alarm_db"
    }
}