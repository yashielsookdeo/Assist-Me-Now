package com.ctrlaltdefeat.assistmenow.recipients

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.data.Models
import com.ctrlaltdefeat.assistmenow.database.Firebase
import com.ctrlaltdefeat.assistmenow.donors.DonorDashboardActivity
import com.ctrlaltdefeat.assistmenow.donors.DonorViewPickUpsActivity
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
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecipientViewDropOffsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var buttonBack: ImageView
    private lateinit var gMap: GoogleMap
    private lateinit var mMap: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var donations: List<Models.FinalDonations>
    private var currentLat: Double = 0.0
    private var currentLong: Double = 0.0
    private var currentPolyline: Polyline? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun handleDirectionsResult(directionsResult: DirectionsResult) {
        val polylineOptions = PolylineOptions()
        val legs = directionsResult.routes[0].legs

        polylineOptions.color(Color.parseColor("#8455c8"))

        for (leg in legs) {
            val steps = leg.steps

            for (step in steps) {
                val points = step.polyline.decodePath()

                for (point in points) {
                    polylineOptions.add(LatLng(point.lat, point.lng))
                }
            }
        }

        currentPolyline = gMap.addPolyline(polylineOptions)
    }

    private fun requestDirectionToMarker(markerPosition: LatLng) {
        val apiKey = "AIzaSyA29WUo0rFVx5ixUeKn9yNYxeGmz2b0S2w"
        val geoApiContext = GeoApiContext.Builder().apiKey(apiKey).build()

        val origin = LatLng(currentLat, currentLong)
        val destination = markerPosition

        GlobalScope.launch(Dispatchers.Main) {
            val directionsResult: DirectionsResult = DirectionsApi.newRequest(geoApiContext).origin("${origin.latitude},${origin.longitude}").destination("${destination.latitude},${destination.longitude}").mode(
                TravelMode.DRIVING).await()

            handleDirectionsResult(directionsResult)
        }
    }

    private fun processDonations() {
        for (donation in donations) {
            var snippetText = ""

            for (item in donation.donation) {
                snippetText = snippetText + item.item + " - " + item.quantity + " | "
            }

            val marker = gMap.addMarker(
                MarkerOptions().position(LatLng(donation.latitude, donation.longitude))
                    .title(donation.name + " | " + donation.creator)
                    .snippet(snippetText)
                    .contentDescription("This is a description?")
            )

            if (marker != null) {
                marker.tag = donation.uid

                gMap.setOnMarkerClickListener { clickedMarker ->
                    val markerID = clickedMarker.tag as? String
                    var markerRequestName = ""

                    for (tempDonation in donations) {
                        if (tempDonation.uid == markerID) {
                            markerRequestName = tempDonation.name

                            break
                        }
                    }

                    if (markerID != null) {
                        Toast.makeText(this, "Getting route to : $markerRequestName", Toast.LENGTH_LONG).show()

                        if (currentPolyline != null) {
                            currentPolyline?.remove()
                        }

                        requestDirectionToMarker(clickedMarker.position)
                    }

                    false
                }
            }
        }
    }

    private fun processLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)

        gMap.clear()
        //gMap.addMarker(MarkerOptions().position(currentLatLng).title("Drop Off Location"))
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))

        currentLat = currentLatLng.latitude
        currentLong = currentLatLng.longitude

        fusedLocationClient.removeLocationUpdates(locationCallback)

        processDonations()
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
        } else {
            requestLocationPermission()
        }
    }

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)

        buttonBack.setOnClickListener {
            val intent = Intent(this, RecipientDashboardActivity::class.java)

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
        setContentView(R.layout.activity_recipient_view_drop_offs)

        initButtons()

        Firebase.getProcessedDonations() { success, donationsList ->
            if (success) {
                donations = donationsList

                initMap(savedInstanceState)
            } else {
                Toast.makeText(this, "Failed to get drop offs - please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0

        getLastLocation()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
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