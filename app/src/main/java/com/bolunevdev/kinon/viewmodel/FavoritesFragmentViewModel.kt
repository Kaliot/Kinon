package com.bolunevdev.kinon.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class FavoritesFragmentViewModel : ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()
    private val compositeDisposable = CompositeDisposable()

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        loadFilms()
    }

    private fun loadFilms() {
        compositeDisposable.add(interactor.getFavoritesFilmsFromDB()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorComplete()
            .subscribe {
                filmsListLiveData.value = it
            })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}