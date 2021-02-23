package com.example.foursquarevenues

import android.app.Application
import com.example.foursquarevenues.app.AppComponent
import com.example.foursquarevenues.app.AppModule
import com.example.foursquarevenues.app.DaggerAppComponent

class FourSquareVenuesApplication: Application()  {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

}