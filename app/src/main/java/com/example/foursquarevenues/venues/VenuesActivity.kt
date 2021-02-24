package com.example.foursquarevenues.venues

import android.annotation.SuppressLint
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
import com.example.foursquarevenues.data.Venue
import com.google.android.gms.location.LocationServices
import javax.inject.Inject

class VenuesActivity : BaseActivity(), VenueView {

    @Inject
    lateinit var getVenuesUseCase: GetVenuesUseCase
    lateinit var presenter: VenuePresenter
    private lateinit var adapter: GenericAdapter<Venue>

    private lateinit var progressBar: ProgressBar
    private lateinit var venuesRv: RecyclerView

    private val handler = Handler(Looper.getMainLooper())


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: Maybe move this up and make sure we don't leak it.
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        progressBar = findViewById(R.id.progressBar)
        venuesRv = findViewById(R.id.venuesRv)

        // TODO: Can we somehow inject this as well
        presenter = VenuePresenterImpl(this, getVenuesUseCase, fusedLocationProviderClient)

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
                        showError(it.name + " clicked")
                    })
                }
            }
            venuesRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            venuesRv.adapter = adapter
        }

    }

    override fun permissionGranted() {
        presenter.getVenues("")
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