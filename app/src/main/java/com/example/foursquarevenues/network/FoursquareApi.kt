package com.example.foursquarevenues.network

import Constants
import com.example.foursquarevenues.BuildConfig
import com.example.foursquarevenues.data.VenuesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FoursquareApi {

    // By default it returns max 50 entries - not implementing pagination for it which
    // will be good to have in a production app
    @GET(
        Constants.BASE_URL + "venues/search?client_id=" + BuildConfig.FOURSQUARE_CLIENT_ID
                + "&client_secret=" + BuildConfig.FOURSQUARE_CLIENT_SECRET + "&v=20210221"
    )
    suspend fun searchVenue(
        @Query("ll") location: String,
        @Query("query") query: String
    ): Response<VenuesResponse>

}