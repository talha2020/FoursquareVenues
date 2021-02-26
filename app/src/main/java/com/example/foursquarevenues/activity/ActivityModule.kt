package com.example.foursquarevenues.activity

import androidx.appcompat.app.AppCompatActivity
import com.example.foursquarevenues.venues.GetLocationUpdatesUseCase
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {
    @Provides
    fun getLocationUpdatesUseCases(activity: AppCompatActivity) =
        GetLocationUpdatesUseCase(activity)
}