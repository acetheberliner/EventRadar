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
import com.exam.eventradar.ui.adapters.CustomInfoWindowAdapter
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
    private var searchedCity: String? = null
    private var isMapReady: Boolean = false

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

        searchedCity = intent.getStringExtra("searched_city")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.back_button)
            .setOnClickListener {
                finish()
            }

        // Recupera i preferiti per i marker
        viewModel.favoriteEvents.observe(this) { favorites ->
            favoriteIds = favorites.map { it.id }.toSet()
            if (isMapReady) {
                updateMarkers()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        isMapReady = true

        // Custom adapter per gli snippet dei marker
        map.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

        if (!searchedCity.isNullOrBlank()) {
            lifecycleScope.launch {
                val latLng = GeocodingUtils.getLocationFromAddress(this@MapActivity, searchedCity!!)
                if (latLng != null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                } else {
                    val italyCenter = LatLng(44.1333, 12.2333)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(italyCenter, 5f))
                }
            }
        } else {
            val italyCenter = LatLng(44.1333, 12.2333)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(italyCenter, 5f))
        }

        checkLocationPermissionAndEnable()

        // Recupera gli eventi
        viewModel.events.observe(this) { allEvents ->
            if (isMapReady) {
                updateMarkers(allEvents)
            }
        }
    }

    private fun updateMarkers(events: List<Event> = viewModel.events.value ?: emptyList()) {
        if (!isMapReady) return

        map.clear()

        // Raggruppa gli eventi in base alla città
        val eventsByCity = events.groupBy { it.location.trim() }

        eventsByCity.forEach { (city, cityEvents) ->
            lifecycleScope.launch {
                val latLng = GeocodingUtils.getLocationFromAddress(this@MapActivity, city)
                latLng?.let {
                    val isFavorite = cityEvents.any { e -> favoriteIds.contains(e.id) }
                    val title = "$city - ${cityEvents.size} evento${if (cityEvents.size > 1) "/i" else ""}"

                    // Build dello snippet multilinea per i marker
                    val snippet = cityEvents.joinToString(separator = "\n") { e ->
                        "• ${e.name} (${e.date})"
                    }

                    map.addMarker(
                        MarkerOptions()
                            .position(it)
                            .title(title)
                            .snippet(snippet)
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
            if (searchedCity.isNullOrBlank()) {
                if (location != null && location.latitude != 37.422 && location.longitude != -122.084) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f))
                } else {
                    val italyCenter = LatLng(44.1333, 12.2333)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(italyCenter, 5f))
                }
            }
        }
    }
}
