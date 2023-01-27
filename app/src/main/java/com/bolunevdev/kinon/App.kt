package com.bolunevdev.kinon

import android.app.Application
import com.bolunevdev.kinon.di.AppComponent
import com.bolunevdev.kinon.di.DaggerAppComponent
import com.bolunevdev.kinon.di.modules.DatabaseModule
import com.bolunevdev.kinon.di.modules.DomainModule
import com.bolunevdev.kinon.di.modules.RemoteModule

class App : Application() {
    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        //Создаем компонент
        dagger = DaggerAppComponent.builder()
            .remoteModule(RemoteModule())
            .databaseModule(DatabaseModule())
            .domainModule(DomainModule(this))
            .build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}