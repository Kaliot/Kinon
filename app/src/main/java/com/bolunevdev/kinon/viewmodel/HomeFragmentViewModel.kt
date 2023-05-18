package com.bolunevdev.kinon.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()
    private val compositeDisposable = CompositeDisposable()
    val showProgressBar: Observable<Boolean>
    val showServerError: MutableLiveData<Boolean> = MutableLiveData()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    private var pageNumber = 1
    private var searchPageNumber = 1

    init {
        App.instance.dagger.inject(this)
        showProgressBar = interactor.observeProgressBar()
        handleError()
        getFilms()
    }

    private fun loadFilms() {
        compositeDisposable.add(interactor.getFilmsFromDB()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorComplete()
            .subscribe {
                filmsListLiveData.value = it
            })
    }

    private fun getFilms() {
        if (isDatabaseNeedToUpdate()) {
            interactor.deleteAllFromDB()
            getFilmsFromApi()
            setNewTimeOfDatabaseUpdate()
        }
        else loadFilms()
    }

    fun getFilmsFromApi() {
        interactor.getFilmsFromApi(pageNumber)
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

    fun getSearchedFilmsFromApi(searchRequest: String) =
        interactor.getSearchedFilms(searchRequest, searchPageNumber)

    fun increaseSearchedPageNumber() {
        searchPageNumber++
    }

    fun resetSearchedPageNumber() {
        searchPageNumber = 1
    }

    private fun handleError() {
        compositeDisposable.add(interactor.observeServerError()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { shouldShowError ->
                showServerError.value = shouldShowError
            })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    companion object {
        const val DEFAULT_TIME = 600_000L
    }
}