package com.exam.eventradar.ui.main

import com.exam.eventradar.ui.favorites.FavoritesActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exam.eventradar.EventRadarApp
import com.exam.eventradar.R
import com.exam.eventradar.ui.adapters.EventAdapter
import com.exam.eventradar.ui.map.MapActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: EventAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textNoEvents: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Impedisce apertura tastiera all'avvio
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        textNoEvents = findViewById(R.id.textNoEvents)

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory((application as EventRadarApp).repository)
        )[MainViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = EventAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val editCity = findViewById<EditText>(R.id.editCity)
        val editDate = findViewById<EditText>(R.id.editDate)
        val buttonFetch = findViewById<Button>(R.id.buttonFetch)

        fun hideKeyboard() {
            val view = currentFocus
            if (view != null) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        buttonFetch.setOnClickListener {
            hideKeyboard()

            val city = editCity.text.toString().trim()
            val date = editDate.text.toString().trim()

            if (city.isBlank()) {
                Toast.makeText(this, "E' necessario indicare una città", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (date.isBlank()) {
                Toast.makeText(this, "E' necessario indicare una data (y-m-d)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.refreshEventsOrFallback(city, date)
        }

        // Gestisce la progressbar per il caricamento delle card degli eventi
        viewModel.progress.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Variabile di stato per evitare situazioni di falso "nessun evento trovato"
        var firstLoadDone = false

        viewModel.events.observe(this) { events ->
            if (!firstLoadDone) {
                // Al primo caricamento sincronizzo currentEvents con gli eventi del db
                viewModel.setCurrentEvents(events)
                firstLoadDone = true
            }
        }

        val switchFutureOnly = findViewById<SwitchCompat>(R.id.switchFutureOnly)

        fun isFutureEvent(dateStr: String): Boolean {
            return try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val eventDate = sdf.parse(dateStr)
                val now = java.util.Calendar.getInstance().time
                eventDate != null && eventDate >= now
            } catch (e: Exception) {
                false
            }
        }

        viewModel.currentEvents.observe(this) { events ->
            val filteredEvents = if (switchFutureOnly.isChecked) {
                events.filter { isFutureEvent(it.date) }
            } else {
                events
            }

            if (firstLoadDone) {
                if (filteredEvents.isEmpty()) {
                    textNoEvents.visibility = View.VISIBLE
                } else {
                    textNoEvents.visibility = View.GONE
                    adapter.submitList(filteredEvents)
                }
            }
        }

        switchFutureOnly.setOnCheckedChangeListener { _, _ ->
            // Forzo refresh della lista visibile
            viewModel.currentEvents.value?.let { events ->
                val filteredEvents = if (switchFutureOnly.isChecked) {
                    events.filter { isFutureEvent(it.date) }
                } else {
                    events
                }

                if (filteredEvents.isEmpty()) {
                    textNoEvents.visibility = View.VISIBLE
                } else {
                    textNoEvents.visibility = View.GONE
                    adapter.submitList(filteredEvents)
                }
            }
        }

        // Fallback message in caso di nessun evento per la città indicata
        viewModel.fallbackMessage.observe(this) { message ->
            if (message.isNotBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        // Carica tutti gli eventi
        viewModel.loadAllEvents {
            firstLoadDone = true
        }

        val buttonOpenMap = findViewById<Button>(R.id.buttonOpenMap)
        buttonOpenMap.setOnClickListener {
            val loadingDialog = com.exam.eventradar.ui.common.LoadingDialogFragment()
            loadingDialog.show(supportFragmentManager, "loading_map")

            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)

            // Delay per permettere alla dialog della mappa di comparire prima di cambiare view
            buttonOpenMap.postDelayed({
                loadingDialog.dismiss()
            }, 2000)
        }

        val buttonFavorites = findViewById<Button>(R.id.buttonFavorites)
        buttonFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        val buttonReset = findViewById<Button>(R.id.buttonReset)

        editDate.setOnClickListener {
            // Apre il DatePicker
            val calendar = java.util.Calendar.getInstance()
            val datePicker = android.app.DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Formatto in yyyy-MM-dd
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    editDate.setText(selectedDate)
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        buttonReset.setOnClickListener {
            editCity.text.clear()
            editDate.text.clear()
            viewModel.loadAllEvents()
        }
    }
}
