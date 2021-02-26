package com.example.foursquarevenues.app

import com.example.foursquarevenues.activity.ActivityComponent
import com.example.foursquarevenues.coroutines.CoroutinesModule
import dagger.Component

@AppScope
@Component(modules = [AppModule::class, CoroutinesModule::class])
interface AppComponent {
    fun newActivityComponentBuilder(): ActivityComponent.Builder
}