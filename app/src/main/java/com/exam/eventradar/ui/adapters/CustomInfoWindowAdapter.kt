package com.exam.eventradar.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.exam.eventradar.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {

    private val window: View = LayoutInflater.from(context).inflate(R.layout.map_info_window, null)

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val titleView = window.findViewById<TextView>(R.id.title)
        val detailsView = window.findViewById<TextView>(R.id.details)

        // Recupero title e snippet dal marker sulla mappa
        titleView.text = marker.title
        detailsView.text = marker.snippet ?: ""

        return window
    }
}
