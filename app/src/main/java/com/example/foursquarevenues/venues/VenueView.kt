package com.example.foursquarevenues.venues

import com.example.foursquarevenues.data.Venue

interface VenueView {
    fun showProgress()
    fun hideProgress()
    fun onVenuesReceived(venues: List<Venue>)
    fun onError()
}