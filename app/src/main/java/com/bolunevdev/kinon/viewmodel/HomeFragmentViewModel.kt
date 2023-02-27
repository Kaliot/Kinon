package com.bolunevdev.kinon.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.domain.Interactor
import com.bolunevdev.kinon.utils.SingleLiveEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class HomeFragmentViewModel : ViewModel() {
    val filmsListFlow: Flow<List<Film>>
    val showProgressBar: Channel<Boolean>
    val showServerError: Channel<Boolean>
    val serverErrorEvent = SingleLiveEvent<String>()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    private var pageNumber = 1

    init {
        App.instance.dagger.inject(this)
        filmsListFlow = interactor.getFilmsFromDB()
        showProgressBar = interactor.progressBarChannel
        showServerError = interactor.serverErrorChannel
        getFilms()
    }

    private fun getFilms() {
        if (isDatabaseNeedToUpdate()) {
            interactor.deleteAllFromDB()
            getFilmsFromApi()
            setNewTimeOfDatabaseUpdate()
        }
    }

    fun getFilmsFromApi() {
        interactor.getFilmsFromApi(pageNumber)
    }

    fun postServerError() {
        serverErrorEvent.postValue(SERVER_ERROR_MESSAGE)
    }

    fun addPreferenceListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        interactor.addSharedPreferencesCategoryChangeListener(listener)
    }

    fun clearFilmsList() {
        interactor.deleteAllFromDB()
        pageNumber = 1
    }

    fun increasePageNumber() {
        pageNumber++
    }

    fun unregisterPreferencesListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        interactor.unregisterSharedPreferencesCategoryChangeListener(listener)
    }

    private fun setNewTimeOfDatabaseUpdate() {
        interactor.setTimeOfUpdate(System.currentTimeMillis())
    }

    private fun getTimeOfDatabaseUpdate() = interactor.getTimeOfUpdate()

    private fun isDatabaseNeedToUpdate(): Boolean {
        val lastTime = getTimeOfDatabaseUpdate()
        val currentTime = System.currentTimeMillis()
        return currentTime - lastTime >= DEFAULT_TIME
    }

    companion object {
        const val DEFAULT_TIME = 600_000L
        const val SERVER_ERROR_MESSAGE = "Не удалось получить данные с сервера!"
    }
}