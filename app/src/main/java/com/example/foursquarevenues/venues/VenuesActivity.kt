package com.example.foursquarevenues.venues

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foursquarevenues.*
import com.example.foursquarevenues.data.Venue
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class VenuesActivity : BaseActivity(), VenueView {

    @Inject
    lateinit var getVenuesUseCase: GetVenuesUseCase
    lateinit var presenter: VenuePresenter
    private lateinit var adapter: GenericAdapter<Venue>

    private lateinit var progressBar: ProgressBar
    private lateinit var venuesRv: RecyclerView

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
                    presenter.getVenues(it)
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

    override fun onError(resId: Int) {
        showError(getString(resId))
    }

    override fun showProgress() {
        progressBar.show()
    }

    override fun hideProgress() {
        progressBar.setGone()
    }


    // location permission code

    private fun invokeLocationAction() {
        when {
            isPermissionsGranted() -> {
                presenter.getVenues("")
            }
            shouldShowRequestPermissionRationale() -> {
                showPermissionsRequiredMessage()
            }
            else -> {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST
        )
    }

    private fun isPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun shouldShowRequestPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_SETTINGS_REQUEST)
            invokeLocationAction()
    }

    companion object {
        const val LOCATION_REQUEST = 100
        const val APP_SETTINGS_REQUEST = 101
    }

    private fun launchApplicationSettingsPage() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(
            intent,
            APP_SETTINGS_REQUEST
        )
    }

    private fun showPermissionsRequiredMessage() {
        MaterialAlertDialogBuilder(this)
            .setMessage(getString(R.string.location_permission_required_message))
            .setPositiveButton(getString(R.string.grant_permission)) { dialog, _ ->
                dialog.dismiss()
                launchApplicationSettingsPage()
            }
            .setCancelable(false)
            .show()
    }

}