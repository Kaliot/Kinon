package com.bolunevdev.remote_module

import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    //Внедряем модуль, нужный для этого компонента
    modules = [RemoteModule::class]
)
interface RemoteComponent : RemoteProvider