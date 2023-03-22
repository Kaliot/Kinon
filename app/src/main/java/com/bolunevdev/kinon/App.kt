package com.bolunevdev.kinon

import android.app.Application
import com.bolunevdev.kinon.di.FacadeComponent


class App : Application() {
    lateinit var dagger: FacadeComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        dagger = getFacade()
    }

    private fun getFacade(): FacadeComponent {
        //Создаем компонент
        return facadeComponent ?: FacadeComponent.init(this).also {
            facadeComponent = it
        }
    }

    companion object {
        //Ссылка для доступа к itemsDao из активити
        lateinit var instance: App
            private set
        private var facadeComponent: FacadeComponent? = null
    }
}