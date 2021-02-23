package com.example.foursquarevenues.venues

interface VenuePresenter {
    fun getVenues(lat: Double, lng: Double, query: String)
    fun onDestroy()
}