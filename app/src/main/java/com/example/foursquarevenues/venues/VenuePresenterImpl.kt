package com.example.foursquarevenues.venues

import com.example.foursquarevenues.network.ApiResponse
import com.example.foursquarevenues.network.ApiResponse.Failure
import com.example.foursquarevenues.network.ApiResponse.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

class VenuePresenterImpl @Inject constructor(
    _view: VenueView,
    private val getVenuesUseCase: GetVenuesUseCase
) : VenuePresenter {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var view: VenueView? = _view

    override fun getVenues(lat: Double, lng: Double, query: String) {

        coroutineScope.launch {
            view?.showProgress()
            try {
                when (val response = getVenuesUseCase.run(lat, lng, query)) {
                    is Success -> {
                        view?.onVenuesReceived(response.data)
                    }
                    Failure -> {
                        view?.onError()
                    }
                }
            } finally {
                view?.hideProgress()
            }
        }
    }

    override fun onDestroy() {
        view = null
        coroutineScope.coroutineContext.cancelChildren()
    }

}