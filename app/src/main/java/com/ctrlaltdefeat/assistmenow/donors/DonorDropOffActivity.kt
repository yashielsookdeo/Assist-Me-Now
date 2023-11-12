package com.ctrlaltdefeat.assistmenow.donors

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.objects.Donation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DonorDropOffActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var buttonBack: ImageView
    private lateinit var buttonDropOff: Button
    private lateinit var gMap: GoogleMap
    private lateinit var mMap: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLat: Double = 0.0
    private var currentLong: Double = 0.0

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun processLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)

        gMap.clear()
        gMap.addMarker(MarkerOptions().position(currentLatLng).title("Drop Off Location"))
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))

        currentLat = currentLatLng.latitude
        currentLong = currentLatLng.longitude

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    val location: Location? = locationResult.lastLocation

                    if (location != null) {
                        processLocation(location)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            /*fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    gMap.clear()
                    gMap.addMarker(MarkerOptions().position(currentLatLng).title("Drop Off Location"))
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))

                    currentLat = currentLatLng.latitude
                    currentLong = currentLatLng.longitude
                } else {
                }
            }*/
        } else {
            requestLocationPermission()
        }
    }

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)
        buttonDropOff = findViewById(R.id.b_adddropoff)

        buttonBack.setOnClickListener {
            Donation.setDropOff(0.0, 0.0)

            val intent = Intent(this, DonorMakeActivity::class.java)

            startActivity(intent)
        }

        buttonDropOff.setOnClickListener {
            Donation.setDropOff(currentLong, currentLat)

            val intent = Intent(this, DonorMakeActivity::class.java)

            startActivity(intent)
        }
    }

    private fun initMap(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 500
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        mMap = findViewById(R.id.m_map)
        mMap.onCreate(savedInstanceState)
        mMap.onResume()
        mMap.getMapAsync(this)

        requestLocationPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_drop_off)

        initMap(savedInstanceState)
        initButtons()
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0

        getLastLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}