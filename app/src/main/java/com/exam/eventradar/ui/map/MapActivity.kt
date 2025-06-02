package com.exam.eventradar.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.exam.eventradar.EventRadarApp
import com.exam.eventradar.R
import com.exam.eventradar.domain.models.Event
import com.exam.eventradar.ui.main.MainViewModel
import com.exam.eventradar.ui.main.MainViewModelFactory
import com.exam.eventradar.utils.GeocodingUtils
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var viewModel: MainViewModel
    private var favoriteIds: Set<String> = emptySet()
    private var isMapReady = false

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) enableUserLocation()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory((application as EventRadarApp).repository)
        )[MainViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.back_button)
            .setOnClickListener {
                finish()
            }

        // Osserva favoriteEvents UNA volta
        viewModel.favoriteEvents.observe(this) { favorites ->
            favoriteIds = favorites.map { it.id }.toSet()
            if (isMapReady) {
                viewModel.events.value?.let { updateMarkers(it) }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        isMapReady = true

        val italyCenter = LatLng(44.1333, 12.2333)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(italyCenter, 5f))

        checkLocationPermissionAndEnable()

        // ✅ Solo ora osserva gli eventi
        viewModel.events.observe(this) { allEvents ->
            if (isMapReady) {
                updateMarkers(allEvents)
            }
        }
    }

    private fun updateMarkers(events: List<Event>) {
        map.clear()

        events.forEach { event ->
            lifecycleScope.launch {
                val latLng = GeocodingUtils.getLocationFromAddress(this@MapActivity, event.location)
                latLng?.let {
                    val isFavorite = favoriteIds.contains(event.id)
                    val title = if (isFavorite) "${event.name} (Preferito)" else event.name

                    map.addMarker(
                        MarkerOptions()
                            .position(it)
                            .title(title)
                            .snippet(event.date)
                            .icon(
                                if (isFavorite)
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                                else
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                            )
                    )
                }
            }
        }
    }

    private fun checkLocationPermissionAndEnable() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            enableUserLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        map.isMyLocationEnabled = true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null && location.latitude != 37.422 && location.longitude != -122.084) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f))
            } else {
                val italyCenter = LatLng(44.1333, 12.2333)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(italyCenter, 5f))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isMapReady = false
    }
}
