package com.example.foursquarevenues.data

import com.google.gson.annotations.SerializedName

// https://developer.foursquare.com/docs/api-reference/venues/search/

data class VenuesResponse(
    @SerializedName("response") val result: Response
)

data class Response(
    @SerializedName("venues") val venues: List<Venue>
)

data class Venue(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("location") val location: Location,
)

data class Location(
    @SerializedName("address") val address: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("distance") val distance: Double
)