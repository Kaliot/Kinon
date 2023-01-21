package com.bolunevdev.kinon

import android.app.Application
import com.bolunevdev.kinon.di.AppComponent
import com.bolunevdev.kinon.di.DaggerAppComponent

class App : Application() {
    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        //Создаем компонент
        dagger = DaggerAppComponent.create()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}