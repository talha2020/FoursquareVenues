package com.example.foursquarevenues.activity

import androidx.appcompat.app.AppCompatActivity
import com.example.foursquarevenues.venues.VenuesActivity
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(venueActivity: VenuesActivity)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: AppCompatActivity): Builder
        fun build(): ActivityComponent
    }

}