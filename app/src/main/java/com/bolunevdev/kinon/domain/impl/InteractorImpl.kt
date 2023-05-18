package com.bolunevdev.kinon.domain.impl

import android.content.Context
import android.content.SharedPreferences
import com.bolunevdev.core_api.entity.Alarm
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.data.PreferenceProvider
import com.bolunevdev.kinon.domain.AlarmRepository
import com.bolunevdev.kinon.domain.FavoriteRepository
import com.bolunevdev.kinon.domain.Interactor
import com.bolunevdev.kinon.domain.MainRepository
import com.bolunevdev.kinon.notifications.WatchLaterNotificationHelper
import com.bolunevdev.remote_module.API
import com.bolunevdev.remote_module.TmdbApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class InteractorImpl @Inject constructor(
    private val repo: MainRepository,
    private val favoriteRepository: FavoriteRepository,
    private val alarmRepository: AlarmRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider,
    private val notificationHelper: WatchLaterNotificationHelper
) : Interactor {

    private val progressBarSubject = BehaviorSubject.create<Boolean>()
    private val serverErrorSubject = BehaviorSubject.create<Boolean>()

    override fun observeProgressBar(): Observable<Boolean> = progressBarSubject.hide()

    override fun observeServerError(): Observable<Boolean> = serverErrorSubject.hide()

    //В конструктор мы будем передавать страницу, которую нужно загрузить (это для пагинации)
    override fun getFilmsFromApi(page: Int) {
        showProgressBar(true)
        showServerError(false)
        //Метод getDefaultCategoryFromPreferences() будет нам получать при каждом запросе нужный нам список фильмов
        retrofitService.getFilms(
            getDefaultCategoryFromPreferences(),
            API.KEY,
            "ru-RU",
            page
        )
            .subscribeOn(Schedulers.io())
            .map { tmdbFilmList ->
                val filmList = tmdbFilmList.results.map {
                    Film(
                        title = it.title,
                        poster = it.posterPath,
                        description = it.overview,
                        rating = it.voteAverage,
                        filmId = it.id
                    )
                }
                repo.putToDb(filmList)
            }
            .doOnError {
                showProgressBar(false)
                showServerError(true)
            }
            .onErrorComplete()
            .doFinally {
                showProgressBar(false)
            }
            .subscribe()
    }

    override fun getSearchedFilms(request: String, searchPageNumber: Int): Observable<List<Film>> {
        return retrofitService.getSearchedFilms(
            API.KEY,
            "ru-RU",
            request,
            searchPageNumber
        )
            .map { TmdbResults ->
                TmdbResults.results.map {
                    Film(
                        title = it.title,
                        poster = it.posterPath,
                        description = it.overview,
                        rating = it.voteAverage,
                        filmId = it.id
                    )
                }
            }
    }

    override fun getFilmsFromDB(): Observable<List<Film>> = repo.getAllFromDB()

    //Метод ля очистки базы данных
    override fun deleteAllFromDB() {
        Completable.fromAction {
            repo.deleteAllFromDB()
        }
            .subscribeOn(Schedulers.io())
            .onErrorComplete()
            .subscribe()
    }

    //Метод для сохранения настроек
    override fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    //Метод для получения настроек
    override fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()

    //Метод для добавления слушателя категорий из настроек
    override fun addSharedPreferencesCategoryChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.addSharedPreferencesCategoryChangeListener(listener)
    }

    //Метод для удаления слушателя категорий из настроек
    override fun unregisterSharedPreferencesCategoryChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterSharedPreferencesCategoryChangeListener(listener)
    }

    override fun setTimeOfUpdate(time: Long) = preferences.setTimeOfDatabaseUpdate(time)

    override fun getTimeOfUpdate(): Long = preferences.getTimeOfDatabaseUpdate()

    override fun getFavoritesFilmsFromDB(): Observable<List<Film>> =
        favoriteRepository.getAllFavoritesFilmsFromDB()

    override fun setFilmAsFavoriteInDB(film: Film) = favoriteRepository.setFilmAsFavoriteInDB(film)

    //Метод ля удаления фильма из базы данных по Id
    override fun setFilmAsNotFavoriteInDB(id: Int) = favoriteRepository.setFilmAsNotFavoriteInDB(id)

    override fun showProgressBar(isShow: Boolean) {
        progressBarSubject.onNext(isShow)
    }

    override fun showServerError(isShow: Boolean) {
        serverErrorSubject.onNext(isShow)
    }

    override fun addAlarm(context: Context, film: Film) {
        notificationHelper.setWatchLaterAlarm(context, film) { alarmTime ->
            putAlarmToDB(film, alarmTime)
        }
    }

    private fun convertAlarmToFilm(alarm: Alarm): Film = Film(
        title = alarm.title,
        poster = alarm.poster,
        description = alarm.description,
        rating = alarm.rating,
        filmId = alarm.filmId
    )

    override fun editAlarm(context: Context, alarm: Alarm) {
        val film = convertAlarmToFilm(alarm)
        addAlarm(context, film)
    }

    override fun cancelAlarm(context: Context, alarm: Alarm) {
        deleteAlarmFromDB(alarm)
        val film = convertAlarmToFilm(alarm)
        notificationHelper.cancelWatchLaterAlarm(context, film)
    }

    private fun putAlarmToDB(film: Film, timeInMillis: Long) {
        val alarm = Alarm(
            timeInMillis = timeInMillis,
            title = film.title,
            poster = film.poster,
            description = film.description,
            rating = film.rating,
            filmId = film.filmId
        )
        Completable.fromAction {
            alarmRepository.putToDb(alarm)
        }
            .subscribeOn(Schedulers.io())
            .onErrorComplete()
            .subscribe()
    }

    private fun deleteAlarmFromDB(alarm: Alarm) {
        Completable.fromAction {
            alarmRepository.deleteFromDB(alarm)
        }
            .subscribeOn(Schedulers.io())
            .onErrorComplete()
            .subscribe()
    }

    override fun getAlarmsFromDB(): Observable<List<Alarm>> = alarmRepository.getAllFromDB()

    override fun deleteOldAlarmsFromDB() {
        val currentTimeInMillis = System.currentTimeMillis()
        getAlarmsFromDB()
            .subscribeOn(Schedulers.io())
            .flatMapIterable { it }
            .filter { alarm -> alarm.timeInMillis < currentTimeInMillis }
            .doOnNext { alarm -> deleteAlarmFromDB(alarm) }
            .subscribe()
    }
}