package com.bolunevdev.kinon.di

import android.app.Application
import com.bolunevdev.core.CoreProvidersFactory
import com.bolunevdev.core_api.AppProvider
import com.bolunevdev.core_api.db.DatabaseProvider
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.di.modules.DomainModule
import com.bolunevdev.kinon.viewmodel.*
import com.bolunevdev.remote_module.DaggerRemoteComponent
import com.bolunevdev.remote_module.RemoteProvider
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    dependencies = [
        AppProvider::class,
        DatabaseProvider::class,
        RemoteProvider::class
    ],
    modules = [DomainModule::class]
)
interface FacadeComponent {
    //метод для того, чтобы появилась внедряемые зависимости в HomeFragmentViewModel
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    //метод для того, чтобы появилась возможность внедрять зависимости в SettingsFragmentViewModel
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)

    //метод для того, чтобы появилась возможность внедрять зависимости в FavoritesFragmentViewModel
    fun inject(FavoritesFragmentViewModel: FavoritesFragmentViewModel)

    //метод для того, чтобы появилась возможность внедрять зависимости в DetailsFragmentViewModel
    fun inject(DetailsFragmentViewModel: DetailsFragmentViewModel)

    fun inject(WatchLaterFragmentViewModel: WatchLaterFragmentViewModel)

    companion object {
        fun init(application: Application): FacadeComponent =
            DaggerFacadeComponent.builder()
                .appProvider(AppComponent.create(application))
                .databaseProvider(
                    CoreProvidersFactory.createDatabaseBuilder(
                        AppComponent.create(application)
                    )
                )
                .remoteProvider(DaggerRemoteComponent.create())
                .domainModule(DomainModule(application))
                .build()
    }

    fun inject(app: App)
}