package com.example.foursquarevenues

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseActivity : AppCompatActivity() {

    // Dependency injection
    private val appComponent get() = (application as FourSquareVenuesApplication).appComponent
    protected val injector get() = appComponent


    /**
     * Location permission code.
     *
     * It may seem like this logic should go to the presenter but in my opinion that will result in a strict
     * coupling between the presenter and the activity since the code uses context pretty extensively.
     * Hence kept it in the activity. By having it in a BaseActivity we can use it across different actvities
     * in the app.
     *
     * Also, used an aggressive approach for asking the permissions here. Assumption was that location permission is
     * required for the app to work. This can off-course be different depending on the app requirements.
     */
    abstract fun permissionGranted()

    protected fun invokeLocationAction() {
        when {
            isPermissionsGranted() -> {
                permissionGranted()
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