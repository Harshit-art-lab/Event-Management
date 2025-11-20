package com.eventmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eventmanagement.adapters.AdminEventsAdapter
import com.eventmanagement.data.FirebaseRepository
import com.eventmanagement.models.Event
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private lateinit var rvAdminEvents: RecyclerView
    private lateinit var btnCreateEvent: Button
    private lateinit var btnLogout: Button
    private lateinit var adapter: AdminEventsAdapter
    private val repository = FirebaseRepository()
    private var events = listOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.title = "Admin Panel"

        rvAdminEvents = findViewById(R.id.rvAdminEvents)
        btnCreateEvent = findViewById(R.id.btnCreateEvent)
        btnLogout = findViewById(R.id.btnAdminLogout)

        // Set up RecyclerView layout manager once
        rvAdminEvents.layoutManager = LinearLayoutManager(this)

        loadEvents()

        btnCreateEvent.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        btnLogout.setOnClickListener {
            repository.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadEvents() {
        lifecycleScope.launch {
            val result = repository.getAllEvents()

            if (result.isSuccess) {
                events = result.getOrNull() ?: emptyList()

                adapter = AdminEventsAdapter(events) { event ->
                    Toast.makeText(
                        this@AdminActivity,
                        "Edit: ${event.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: Implement edit functionality later
                }

                rvAdminEvents.adapter = adapter
            } else {
                Toast.makeText(
                    this@AdminActivity,
                    "Failed to load events",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh event list when returning to this activity
        loadEvents()
    }
}