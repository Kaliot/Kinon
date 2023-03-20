package com.bolunevdev.remote_module


interface RemoteProvider {
    fun provideRemoteModule(): TmdbApi
}