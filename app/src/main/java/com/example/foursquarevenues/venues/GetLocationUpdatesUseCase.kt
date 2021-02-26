package com.example.foursquarevenues.venues

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

/**
 * This UseCase is used to get a Flow of location.
 * Update interval and accuracy can be adjust.
 */
@ExperimentalCoroutinesApi
@SuppressLint("MissingPermission")
class GetLocationUpdatesUseCase(context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun run() = callbackFlow<LocationModel> {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    sendBlocking(LocationModel(location.latitude, location.longitude))
                }
            }
        }
        registerListener(locationCallback)
        awaitClose { unregisterListener(locationCallback) }
    }


    private fun registerListener(locationCallback: LocationCallback) {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun unregisterListener(locationCallback: LocationCallback) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    data class LocationModel(
        val latitude: Double,
        val longitude: Double
    )

}