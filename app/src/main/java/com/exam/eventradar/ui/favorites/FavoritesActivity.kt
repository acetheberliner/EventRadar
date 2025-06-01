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

class FavoritesActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as EventRadarApp).repository)
    }

    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFavorites)
        adapter = EventAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.favoriteEvents.observe(this) { entityList ->
            val eventList = entityList.map { entity ->
                Event(
                    id = entity.id,
                    name = entity.name,
                    date = entity.date,
                    location = entity.location,
                    imageUrl = entity.imageUrl
                )
            }
            adapter.submitList(eventList)
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.back_button)
            .setOnClickListener {
                finish()
            }
    }
}
