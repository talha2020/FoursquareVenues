package com.example.foursquarevenues.venues

interface VenuePresenter {
    fun getVenues(query: String)
    fun startLocationUpdates()
    fun onDestroy()
}