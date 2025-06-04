package com.exam.eventradar.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.exam.eventradar.R
import com.exam.eventradar.domain.models.Event
import com.exam.eventradar.ui.details.EventDetailsActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventAdapter(private var events: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewEvent: ImageView = itemView.findViewById(R.id.imageViewEvent)
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val textViewLocation: TextView = itemView.findViewById(R.id.textViewLocation)
        private val textExpired: TextView = itemView.findViewById(R.id.textExpired)

        fun bind(event: Event) {
            textViewName.text = event.name
            textViewDate.text = "🗓 ${event.date}"
            textViewLocation.text = "📍 ${event.location}"

            Glide.with(itemView.context)
                .load(event.imageUrl)
                .error(R.drawable.placeholder_image)
                .placeholder(R.drawable.placeholder_image)
                .into(imageViewEvent)

            // Controllo se un evento è già avvenuto (passato)
            val eventDate = try {
                LocalDate.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: Exception) {
                null
            }

            val today = LocalDate.now()

            if (eventDate != null && eventDate.isBefore(today)) {
                textExpired.visibility = View.VISIBLE
            } else {
                textExpired.visibility = View.GONE
            }

            // Il click sulla card conduce ai dettagli dell'evento corrispondente
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, EventDetailsActivity::class.java).apply {
                    putExtra("event_id", event.id)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    fun submitList(newList: List<Event>) {
        events = newList
        notifyDataSetChanged()
    }
}
