package com.example.foursquarevenues.venues

import com.example.foursquarevenues.R
import com.example.foursquarevenues.coroutines.CoroutinesDispatcherProvider
import com.example.foursquarevenues.network.ApiResponse.Failure
import com.example.foursquarevenues.network.ApiResponse.Success
import com.example.foursquarevenues.venues.GetLocationUpdatesUseCase.LocationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
class VenuePresenterImpl @Inject constructor(
    _view: VenueView,
    private val getVenuesUseCase: GetVenuesUseCase,
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    dispatcherProvider: CoroutinesDispatcherProvider
) : VenuePresenter {

    private var view: VenueView? = _view
    private var location: LocationModel? = null
        set(value) {
            if (field == null) {
                // Get the venues when we receive location first time
                value?.let { location ->
                    getVenues(location.latitude, location.longitude, "")
                }
            }
            field = value
        }

    private val coroutineScope = CoroutineScope(dispatcherProvider.main)

    override fun getVenues(query: String) {
        location?.also {
            getVenues(it.latitude, it.longitude, query)
        } ?: view?.onError(R.string.cannot_get_location)
    }

    override fun startLocationUpdates() {
        coroutineScope.launch {
            getLocationUpdatesUseCase.run().collect {
                location = it
            }
        }
    }

    override fun onDestroy() {
        view = null
        coroutineScope.cancel()
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