package com.example.foursquarevenues

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    private val appComponent get() = (application as FourSquareVenuesApplication).appComponent

    protected val injector get() = appComponent
}