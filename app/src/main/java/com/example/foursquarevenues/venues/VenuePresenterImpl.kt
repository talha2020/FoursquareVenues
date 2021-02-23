package com.example.foursquarevenues.venues

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

class VenuePresenterImpl @Inject constructor(
    _view: VenueView,
    private val getVenuesUseCase: GetVenuesUseCase
): VenuePresenter {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var view: VenueView? = _view

    override fun getVenues(lat: Double, lng: Double, query: String) {
        coroutineScope.launch {
            getVenuesUseCase.run(lat, lng, query)
        }
    }

    override fun onDestroy() {
        view = null
        coroutineScope.coroutineContext.cancelChildren()
    }

}