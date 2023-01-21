package com.bolunevdev.kinon.di

import com.bolunevdev.kinon.di.modules.DatabaseModule
import com.bolunevdev.kinon.di.modules.DomainModule
import com.bolunevdev.kinon.di.modules.RemoteModule
import com.bolunevdev.kinon.viewmodel.HomeFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    //Внедряем все модули, нужные для этого компонента
    modules = [
        RemoteModule::class,
        DatabaseModule::class,
        DomainModule::class
    ]
)
interface AppComponent {
    //метод для того, чтобы появилась внедряемые зависимости в HomeFragmentViewModel
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
}