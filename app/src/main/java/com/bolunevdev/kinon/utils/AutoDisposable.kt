package com.bolunevdev.kinon.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

//Наследуем от DefaultLifecycleObserver
class AutoDisposable : DefaultLifecycleObserver {
    //Используем CompositeDisposable для отмены всех Observable
    private lateinit var compositeDisposable: CompositeDisposable

    //Сюда передаем ссылку на ЖЦ компонента, за которым будет слежение
    fun bindTo(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        compositeDisposable = CompositeDisposable()
    }

    //Метод для добавления Observable в CompositeDisposable
    fun add(disposable: Disposable) {
        if (::compositeDisposable.isInitialized) {
            compositeDisposable.add(disposable)
        } else {
            throw NotImplementedError(NOT_IMPLEMENTED_ERROR_MESSAGE)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        compositeDisposable.dispose()
    }

    companion object {
        private const val NOT_IMPLEMENTED_ERROR_MESSAGE =
            "must bind AutoDisposable to a Lifecycle first"
    }
}

//Экстеншн
fun Disposable.addTo(autoDisposable: AutoDisposable) {
    autoDisposable.add(this)
}