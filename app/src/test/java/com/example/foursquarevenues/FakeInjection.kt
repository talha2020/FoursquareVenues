package com.example.foursquarevenues

import com.example.foursquarevenues.coroutines.CoroutinesDispatcherProvider
import kotlinx.coroutines.Dispatchers

fun provideFakeCoroutinesDispatcherProvider(): CoroutinesDispatcherProvider =
    CoroutinesDispatcherProvider(
        Dispatchers.Unconfined,
        Dispatchers.Unconfined,
        Dispatchers.Unconfined
    )