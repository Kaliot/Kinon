package com.bolunevdev.kinon.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Film
import com.bolunevdev.kinon.domain.Interactor
import com.bolunevdev.kinon.view.fragments.HomeFragment

class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()
    private val filmsList = mutableListOf<Film>()
    //Инициализируем интерактор
    private var interactor: Interactor = App.instance.interactor
    private var pageNumber = 1

    init {
        getFilmsFromApi()
    }

    fun getFilmsFromApi() {
        interactor.getFilmsFromApi(pageNumber, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmsList.addAll(films)
                filmsListLiveData.postValue(filmsList)
                HomeFragment.isLoading = false
            }
            override fun onFailure() {
            }
        })
    }

    fun increasePageNumber() {
        pageNumber++
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}