package com.example.foursquarevenues.venues

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import com.example.foursquarevenues.BaseActivity
import com.example.foursquarevenues.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

class VenuesActivity : BaseActivity(), VenueView {

    @Inject lateinit var getVenuesUseCase: GetVenuesUseCase

    lateinit var presenter: VenuePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                query?.let {
                    presenter.getVenues(40.7243, -74.0018, it)
                    //Log.d("VenuesActivity", it)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                //query?.let { Log.d("VenuesActivity", it) }
                return false
            }

        })
        return true
    }

    override fun showProgress() {
        TODO("Not yet implemented")
    }

    override fun hideProgress() {
        TODO("Not yet implemented")
    }


}