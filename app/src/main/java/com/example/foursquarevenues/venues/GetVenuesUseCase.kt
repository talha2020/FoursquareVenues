package com.example.foursquarevenues.venues

import com.example.foursquarevenues.data.Venue
import com.example.foursquarevenues.network.ApiResponse
import com.example.foursquarevenues.network.FoursquareApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetVenuesUseCase @Inject constructor(private val foursquareApi: FoursquareApi) {

    suspend fun run(
        lat: Double,
        lng: Double,
        query: String
    ): ApiResponse<List<Venue>> =
        withContext(Dispatchers.IO) {
            try {
                val response = foursquareApi.searchVenue("$lat,$lng", query)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext ApiResponse.Success(response.body()!!.result.venues.sortedBy { it.location.distance })
                } else {
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