package com.example.foursquarevenues.venues

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foursquarevenues.*
import com.example.foursquarevenues.data.Venue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

class VenuesActivity : BaseActivity(), VenueView {

    @Inject
    lateinit var getVenuesUseCase: GetVenuesUseCase

    lateinit var presenter: VenuePresenter

    private lateinit var progressBar: ProgressBar
    private lateinit var venuesRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        venuesRv = findViewById(R.id.venuesRv)

        // TODO: Can we somehow inject this as well
        presenter = VenuePresenterImpl(this, getVenuesUseCase)
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
                query?.let {
                    presenter.getVenues(40.7243, -74.0018, it)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }

        })
        return true
    }

    override fun onVenuesReceived(venues: List<Venue>) {
        if (venues.isNullOrEmpty()) {
            showError(getString(R.string.no_venues_found))
            return
        }
        //TODO: Avoid creating a new adapter everytime
        // normally i would update the recycler view items and then notify the adapter. Leaving it like this due to shortage of time.

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

    override fun onError() {
        showError("Unknown error.")
    }

    override fun showProgress() {
        progressBar.show()
    }

    override fun hideProgress() {
        progressBar.setGone()
    }


}