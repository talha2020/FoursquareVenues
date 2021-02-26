package com.example.foursquarevenues.venues

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foursquarevenues.*
import com.example.foursquarevenues.coroutines.CoroutinesDispatcherProvider
import com.example.foursquarevenues.data.Venue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class VenuesActivity : BaseActivity(), VenueView {

    @Inject
    lateinit var getVenuesUseCase: GetVenuesUseCase

    @Inject
    lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    @Inject
    lateinit var getLocationUpdatesUseCase: GetLocationUpdatesUseCase

    lateinit var presenter: VenuePresenter
    private lateinit var adapter: GenericAdapter<Venue>

    private lateinit var progressBar: ProgressBar
    private lateinit var venuesRv: RecyclerView

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        venuesRv = findViewById(R.id.venuesRv)

        // Ideally this should be injected by dagger as well - leaving these as is to avoid over optimization for this assignment
        presenter = VenuePresenterImpl(
            this,
            getVenuesUseCase,
            getLocationUpdatesUseCase,
            dispatcherProvider
        )

        invokeLocationAction()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_venues_activity, menu)

        val searchMenuItem = menu?.findItem(R.id.search)
        val searchView = searchMenuItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.also { fetchVenuesForQuery(it) }
                return true
            }

        })
        return true
    }

    private fun fetchVenuesForQuery(query: String) {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            presenter.getVenues(query)
        }, 500)
    }

    override fun onVenuesReceived(venues: List<Venue>) {
        if (venues.isNullOrEmpty()) {
            showError(getString(R.string.no_venues_found))
            return
        }

        if (::adapter.isInitialized) {
            adapter.setItems(venues)
        } else {
            val adapter = object : GenericAdapter<Venue>(venues) {
                override fun getLayoutId(position: Int, obj: Venue): Int {
                    return R.layout.venue_list_item
                }

                override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
                    return VenuesListViewHolder(view, onItemClick = {
                        showError("${it.name} tapped")
                    })
                }
            }
            venuesRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            venuesRv.adapter = adapter
        }

    }

    override fun permissionGranted() {
        // In case of multiple permissions we an pass the permission type here
        // Kept it simple here
        presenter.startLocationUpdates()
    }

    override fun onError(resId: Int) {
        showError(getString(resId))
    }

    override fun showProgress() {
        progressBar.show()
    }

    override fun hideProgress() {
        progressBar.setGone()
    }

}