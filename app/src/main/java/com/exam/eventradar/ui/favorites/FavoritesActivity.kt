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

class FavoritesActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as EventRadarApp).repository)
    }

    private lateinit var adapter: EventAdapter
    private var isOrderByDate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFavorites)
        adapter = EventAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val switchOrderByDate = findViewById<SwitchMaterial>(R.id.switchOrderByDate)

        // Quando cambio lo switch, aggiorno la lista ordinata
        switchOrderByDate.setOnCheckedChangeListener { _, isChecked ->
            isOrderByDate = isChecked
            // Ritrigger il LiveData per riordinare
            updateFavoriteList(viewModel.favoriteEvents.value ?: emptyList())
        }

        // Osserva i preferiti
        viewModel.favoriteEvents.observe(this) { entityList ->
            updateFavoriteList(entityList)
        }

        // Pulsante indietro
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.back_button)
            .setOnClickListener {
                finish()
            }
    }

    private fun updateFavoriteList(entityList: List<com.exam.eventradar.data.local.entities.EventEntity>) {
        // Conversione a domain model
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

        // Se lo switch è ON, ordina per data
        val sortedList = if (isOrderByDate) {
            eventList.sortedBy { it.date }
        } else {
            eventList
        }

        // Aggiorna la lista nella RecyclerView
        adapter.submitList(sortedList)
    }
}
