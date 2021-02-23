package com.example.foursquarevenues.app

import com.example.foursquarevenues.venues.VenuesActivity
import dagger.Component

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent {
    //TODO: Maybe have a sub component for activity injection
    fun inject(venueActivity: VenuesActivity)
}