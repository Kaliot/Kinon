package com.bolunevdev.kinon.di.modules

import com.bolunevdev.kinon.domain.Interactor
import com.bolunevdev.kinon.domain.impl.InteractorImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class InteractorModule {

    @Singleton
    @Binds
    abstract fun bindInteractor(interactorImpl: InteractorImpl): Interactor
}