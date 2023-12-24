package com.candra.dicodingstoryapplication.activity.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.data.result.Result
import com.candra.dicodingstoryapplication.databinding.ActivityMapsBinding
import com.candra.dicodingstoryapplication.viewModelFactory.StoryViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.runBlocking

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel by viewModels<MapsViewModel> {
        StoryViewModelFactory.getInstance(this@MapsActivity, true)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.backToMainFromMaps.setOnClickListener {
            onBackPressed()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapsStyle()
        getMyLocationNow()
        addMaker()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGpsEnabled || isNetworkEnabled
    }

    private fun addMaker() {
        runBlocking {
            mapsViewModel.getStoriesWithLocation().observe(this@MapsActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        result.data.forEach { data ->
                            val latLng = LatLng(data.lat!!, data.lon!!)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(data.name)
                                    .snippet(data.description)
                            )
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            this@MapsActivity,
                            result.error.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbMaps.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getMyLocationNow()
        }
    }

    private fun getMyLocationNow() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                    loc?.let {
                        val lat = loc.latitude
                        val long = loc.longitude
                        val mySpace = LatLng(lat, long)

                        mMap.addMarker(
                            MarkerOptions()
                                .position(mySpace)
                                .title(getString(R.string.my_location))
                                .snippet(getString(R.string.location_desc))
                        )
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mySpace, 15f))
                    }
                }
            } else {
                val dicodingSpace = LatLng(-6.8957643, 107.6338462)
                mMap.addMarker(
                    MarkerOptions()
                        .position(dicodingSpace)
                        .title(getString(R.string.default_location_name))
                        .snippet(getString(R.string.default_location_address))
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))
            }

        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapsStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
            if (!success) {
                Log.e(TAG, getString(R.string.error_parsing_style))
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.cant_find_style), e)
        }
    }

    private companion object {
        const val TAG = "MapsActivity"
    }
}