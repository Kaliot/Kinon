package com.bolunevdev.core_impl

import com.bolunevdev.core_api.AppProvider
import com.bolunevdev.core_api.db.DatabaseProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [AppProvider::class],
    modules = [DatabaseModule::class]
)
interface DatabaseComponent : DatabaseProvider