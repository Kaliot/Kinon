package com.bolunevdev.core

import com.bolunevdev.core_api.AppProvider
import com.bolunevdev.core_api.db.DatabaseProvider
import com.bolunevdev.core_impl.DaggerDatabaseComponent


object CoreProvidersFactory {
    fun createDatabaseBuilder(appProvider: AppProvider): DatabaseProvider {
        return DaggerDatabaseComponent.builder().appProvider(appProvider).build()
    }
}