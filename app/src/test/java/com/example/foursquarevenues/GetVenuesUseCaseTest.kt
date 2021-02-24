package com.example.foursquarevenues

import com.example.foursquarevenues.data.Location
import com.example.foursquarevenues.data.Venue
import com.example.foursquarevenues.data.VenuesResponse
import com.example.foursquarevenues.network.ApiResponse
import com.example.foursquarevenues.network.FoursquareApi
import com.example.foursquarevenues.venues.GetVenuesUseCase
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.Response
import com.example.foursquarevenues.data.Response as VenueResponse1

class GetVenuesUseCaseTest {

    private val foursquareApi: FoursquareApi = mock {
        onBlocking { searchVenue(any(), any()) } doReturn Response.success(getFakeVenueResponse())
    }

    private val getVenuesUseCase: GetVenuesUseCase

    init {
        getVenuesUseCase = GetVenuesUseCase(foursquareApi)
    }

    @Test
    fun `should return error when request fails`() = runBlocking {
        whenever(foursquareApi.searchVenue(any(), any())).thenReturn(
            Response.error(
                404,
                ResponseBody.create(null, "")
            )
        )
        val venuesResponse = getVenuesUseCase.run(40.0, 55.0, "pizza")

        assertThat(venuesResponse is ApiResponse.Success).isFalse()
        assertThat(venuesResponse is ApiResponse.Failure).isTrue()
    }

    @Test
    fun `should return correct number of results`() = runBlocking {
        val venuesResponse = getVenuesUseCase.run(40.0, 55.0, "pizza")

        assertThat(venuesResponse is ApiResponse.Success).isTrue()

        val venues = (venuesResponse as ApiResponse.Success).data
        assertThat(venues.size).isEqualTo(3)
    }

    @Test
    fun `should return results in sorted order`() = runBlocking {
        val venuesResponse = getVenuesUseCase.run(40.0, 55.0, "pizza")

        assertThat(venuesResponse is ApiResponse.Success).isTrue()

        val venues = (venuesResponse as ApiResponse.Success).data
        assertThat(venues[0].location.distance).isEqualTo(400)
        assertThat(venues[1].location.distance).isEqualTo(1200)
        assertThat(venues[2].location.distance).isEqualTo(2000)
    }

    // This can be read from a file as well where we can have larger data set
    // kept it simple for the purpose of this assignment
    private fun getFakeVenueResponse(): VenuesResponse {
        return VenuesResponse(
            VenueResponse1(
                listOf(
                    Venue(
                        "1",
                        "Test Venue 1",
                        Location(
                            "21 no street",
                            40.123,
                            55.321,
                            1200.0
                        )
                    ),
                    Venue(
                        "2",
                        "Test Venue 2",
                        Location(
                            "22 no street",
                            40.123,
                            55.321,
                            400.0
                        )
                    ),
                    Venue(
                        "3",
                        "Test Venue 3",
                        Location(
                            "23 no street",
                            40.123,
                            55.321,
                            2000.0
                        )
                    )
                )
            )
        )
    }
}