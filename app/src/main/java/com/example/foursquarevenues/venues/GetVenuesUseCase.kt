package com.example.foursquarevenues.venues

import com.example.foursquarevenues.coroutines.CoroutinesDispatcherProvider
import com.example.foursquarevenues.data.Venue
import com.example.foursquarevenues.network.ApiResponse
import com.example.foursquarevenues.network.FoursquareApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetVenuesUseCase @Inject constructor(
    private val foursquareApi: FoursquareApi,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {
    suspend fun run(
        lat: Double,
        lng: Double,
        query: String
    ): ApiResponse<List<Venue>> =
        withContext(dispatcherProvider.io) {
            try {
                val response = foursquareApi.searchVenue("$lat,$lng", query)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext ApiResponse.Success(response.body()!!.result.venues.sortedBy { it.location.distance })
                } else {
                    // Not doing full error handling here and just returning the Failure object
                    // In production app I'll consider all the scenarios and make sure all of them are handled properly.
                    return@withContext ApiResponse.Failure
                }
            } catch (t: Throwable) {
                if (t !is CancellationException) {
                    return@withContext ApiResponse.Failure
                } else {
                    throw t
                }
            }
        }
}