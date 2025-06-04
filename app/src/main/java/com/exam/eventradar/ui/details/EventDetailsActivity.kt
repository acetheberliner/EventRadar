package com.exam.eventradar.ui.details

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.exam.eventradar.EventRadarApp
import com.exam.eventradar.R
import com.exam.eventradar.domain.models.Event
import com.exam.eventradar.ui.main.MainViewModel
import com.exam.eventradar.ui.main.MainViewModelFactory

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var currentEvent: Event
    private lateinit var buttonSave: Button
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory((application as EventRadarApp).repository)
        )[MainViewModel::class.java]

        val imageView = findViewById<ImageView>(R.id.imageViewDetails)
        val textName = findViewById<TextView>(R.id.textViewDetailName)
        val textDate = findViewById<TextView>(R.id.textViewDetailDate)
        val textLocation = findViewById<TextView>(R.id.textViewDetailLocation)
        val textDescription = findViewById<TextView>(R.id.textViewDetailDescription)
        val textLink = findViewById<TextView>(R.id.textViewDetailLink)
        val textContacts = findViewById<TextView>(R.id.textViewDetailContacts)

        buttonSave = findViewById(R.id.buttonSaveFavorite)

        val eventId = intent.getStringExtra("event_id") ?: return

        viewModel.events.observe(this) { list ->
            val event = list.find { it.id == eventId }
            if (event != null) {
                currentEvent = event

                textName.text = event.name
                textDate.text = event.date
                textLocation.text = event.location
                textDescription.text = event.description ?: "Nessuna descrizione disponibile"
                textLink.text = event.externalLink ?: "Nessun link disponibile"
                textContacts.text = event.contacts ?: "Nessun contatto disponibile"

                Glide.with(this)
                    .load(event.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageView)

                updateFavoriteUI()
            }
        }

        buttonSave.setOnClickListener {
            viewModel.setFavoriteStatus(currentEvent.id, !isFavorite)
            isFavorite = !isFavorite
            updateFavoriteUI()
            Toast.makeText(
                this,
                if (isFavorite) "Aggiunto ai preferiti" else "Rimosso dai preferiti",
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.favoriteEvents.observe(this) { favorites ->
            isFavorite = favorites.any { it.id == eventId }
            updateFavoriteUI()
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.back_button)
            .setOnClickListener {
                finish()
            }
    }

    private fun updateFavoriteUI() {
        buttonSave.text = if (isFavorite) "Rimuovi" else "Aggiungi ai preferiti"
        buttonSave.setBackgroundColor(
            getColor(if (isFavorite) R.color.red else R.color.yellow)
        )
    }
}
