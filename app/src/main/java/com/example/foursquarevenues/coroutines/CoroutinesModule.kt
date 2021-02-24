package com.example.foursquarevenues.coroutines

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
object CoroutinesModule {

    @Provides
    fun provideCoroutinesDispatcherProvider() = CoroutinesDispatcherProvider(
        Dispatchers.Main,
        Dispatchers.Default,
        Dispatchers.IO
    )
}