package com.example.foursquarevenues

import com.example.foursquarevenues.data.Location
import com.example.foursquarevenues.data.Venue
import com.example.foursquarevenues.network.ApiResponse
import com.example.foursquarevenues.venues.*
import com.example.foursquarevenues.venues.GetLocationUpdatesUseCase.LocationModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
/**
 * Tests the venue presenter. Since we don't have a lot of logic besides basic plumbing,
 * It tests if the right methods are called with right parameters in response to the user inputs.
 */
class VenuePresenterTest {

    private val view: VenueView = mock()
    private val getVenuesUseCase: GetVenuesUseCase = mock()
    private val locationUpdatesFlow = MutableStateFlow(LocationModel(40.0, 50.0))

    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase = mock {
        on { run() } doReturn locationUpdatesFlow
    }

    private val venuePresenter: VenuePresenter

    init {
        venuePresenter = VenuePresenterImpl(
            view,
            getVenuesUseCase,
            getLocationUpdatesUseCase,
            provideFakeCoroutinesDispatcherProvider()
        )
    }

    @Test
    fun `should call show and hide progress exactly once`() = runBlockingTest {
        venuePresenter.getVenues("pizza")
        verify(view, times(1)).showProgress()
        verify(view, times(1)).hideProgress()
    }

    @Test
    fun `should call getVenues with right parameters`() = runBlockingTest {
        venuePresenter.getVenues("pizza")
        verify(getVenuesUseCase, times(1)).run(40.0, 50.0, "pizza")
    }

    @Test
    fun `should call onError with correct string id in case of error`() = runBlockingTest {
        whenever(getVenuesUseCase.run(any(), any(), any())).thenReturn(ApiResponse.Failure)
        venuePresenter.getVenues("pizza")
        verify(view, times(1)).onError(R.string.network_error)
    }

    @Test
    fun `should call onVenuesReceived with correct data in case of success`() = runBlockingTest {
        whenever(getVenuesUseCase.run(any(), any(), any())).thenReturn(
            ApiResponse.Success(
                getFakeVenueList()
            )
        )
        venuePresenter.getVenues("pizza")
        verify(view, times(1)).onVenuesReceived(getFakeVenueList())
    }

    private fun getFakeVenueList(): List<Venue> {
        return listOf(
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
        )
    }

}