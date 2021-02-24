package com.example.foursquarevenues.venues

import android.annotation.SuppressLint
import android.location.Location
import com.example.foursquarevenues.R
import com.example.foursquarevenues.network.ApiResponse.Failure
import com.example.foursquarevenues.network.ApiResponse.Success
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
class VenuePresenterImpl @Inject constructor(
    _view: VenueView,
    private val getVenuesUseCase: GetVenuesUseCase,
    private val fusedLocationClient: FusedLocationProviderClient
) : VenuePresenter {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var view: VenueView? = _view

    override fun getVenues(query: String) {
        view?.showProgress()
        try {
            //TODO: Location returned can be null if location cache has been cleared - handle null and explain the reasoning
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.also {
                    getVenues(it.latitude, it.longitude, query)
                }?: view?.onError(R.string.cannot_get_location)
            }
        } finally {
            view?.hideProgress()
        }
    }

    override fun onDestroy() {
        view = null
        coroutineScope.coroutineContext.cancelChildren()
    }

    private fun getVenues(lat: Double, lng: Double, query: String) {
        coroutineScope.launch {
            view?.showProgress()
            try {
                when (val response =
                    getVenuesUseCase.run(lat, lng, query)) {
                    is Success -> {
                        view?.onVenuesReceived(response.data)
                    }
                    Failure -> {
                        view?.onError(R.string.network_error)
                    }
                }
            } finally {
                view?.hideProgress()
            }
        }
    }
}