package com.bolunevdev.kinon.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.domain.Interactor
import com.bolunevdev.kinon.utils.SingleLiveEvent
import javax.inject.Inject


class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData: LiveData<List<Film>>
    val showProgressBar: MutableLiveData<Boolean> = MutableLiveData()
    val serverErrorEvent = SingleLiveEvent<String>()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    private var pageNumber = 1

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDB()
        getFilms()
    }

    private fun getFilms() {
        if (isDatabaseNeedToUpdate()) {
            showProgressBar.postValue(true)
            interactor.tryToUpdateFilmsFromDB(object : ApiCallback {
                override fun onSuccess() {
                    setNewTimeOfDatabaseUpdate()
                    showProgressBar.postValue(false)
                }

                override fun onFailure() {
                    showProgressBar.postValue(false)
                    postServerError()
                }
            })
        }
    }

    fun getFilmsFromApi() {
        showProgressBar.postValue(true)
        interactor.getFilmsFromApi(pageNumber, object : ApiCallback {
            override fun onSuccess() {
                showProgressBar.postValue(false)
            }

            override fun onFailure() {
                showProgressBar.postValue(false)
                postServerError()
            }
        })
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

    interface ApiCallback {
        fun onSuccess()
        fun onFailure()
    }

    companion object {
        const val DEFAULT_TIME = 600_000L
        const val SERVER_ERROR_MESSAGE = "Не удалось получить данные с сервера!"
    }
}