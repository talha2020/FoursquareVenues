package com.example.foursquarevenues.app

import com.example.foursquarevenues.coroutines.CoroutinesModule
import com.example.foursquarevenues.venues.VenuesActivity
import dagger.Component

@AppScope
@Component(modules = [AppModule::class, CoroutinesModule::class])
interface AppComponent {
    fun inject(venueActivity: VenuesActivity)
}