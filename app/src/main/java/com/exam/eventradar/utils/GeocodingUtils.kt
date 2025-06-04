package com.exam.eventradar.utils

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.util.*

object GeocodingUtils {
    fun getLocationFromAddress(context: Context, address: String): LatLng? {
        return try {
            val geo = Geocoder(context, Locale.getDefault())
            val addresses = geo.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                LatLng(location.latitude, location.longitude)
            } else null
        } catch (e: Exception) {
            Log.e("Geocoding", "Errore getLocationFromAddress: ${e.localizedMessage}")
            null
        }
    }
}
