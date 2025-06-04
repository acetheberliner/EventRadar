package com.exam.eventradar.ui.favorites

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exam.eventradar.EventRadarApp
import com.exam.eventradar.R
import com.exam.eventradar.domain.models.Event
import com.exam.eventradar.ui.adapters.EventAdapter
import com.exam.eventradar.ui.main.MainViewModel
import com.exam.eventradar.ui.main.MainViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.*

class FavoritesActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as EventRadarApp).repository)
    }

    private lateinit var adapter: EventAdapter
    private var isOrderByDate = false
    private var isFutureOnly = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFavorites)
        adapter = EventAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val switchOrderByDate = findViewById<SwitchMaterial>(R.id.switchOrderByDate)
        val switchFutureOnly = findViewById<SwitchMaterial>(R.id.switchFutureOnlyFavorites)

        // Ordina per data
        switchOrderByDate.setOnCheckedChangeListener { _, isChecked ->
            isOrderByDate = isChecked
            updateFavoriteList(viewModel.favoriteEvents.value ?: emptyList())
        }

        // Nascondi eventi passati
        switchFutureOnly.setOnCheckedChangeListener { _, isChecked ->
            isFutureOnly = isChecked
            updateFavoriteList(viewModel.favoriteEvents.value ?: emptyList())
        }

        // Legge i preferiti
        viewModel.favoriteEvents.observe(this) { entityList ->
            updateFavoriteList(entityList)
        }

        // Pulsante indietro floating
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.back_button)
            .setOnClickListener {
                finish()
            }
    }

    private fun updateFavoriteList(entityList: List<com.exam.eventradar.data.local.entities.EventEntity>) {
        val eventList = entityList.map { entity ->
            Event(
                id = entity.id,
                name = entity.name,
                date = entity.date,
                location = entity.location,
                imageUrl = entity.imageUrl,
                description = entity.description,
                externalLink = entity.externalLink,
                contacts = entity.contacts
            )
        }

        // Nascondi eventi passati
        val filteredList = if (isFutureOnly) {
            eventList.filter { isFutureEvent(it.date) }
        } else {
            eventList
        }

        // Ordina per data
        val finalList = if (isOrderByDate) {
            filteredList.sortedBy { it.date }
        } else {
            filteredList
        }

        adapter.submitList(finalList)
    }

    private fun isFutureEvent(dateStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val eventDate = sdf.parse(dateStr)
            val now = Calendar.getInstance().time
            eventDate != null && eventDate >= now
        } catch (e: Exception) {
            false
        }
    }
}
