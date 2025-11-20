package com.eventmanagement

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eventmanagement.data.FirebaseRepository
import kotlinx.coroutines.launch

class EventDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Event Details"

        val eventId = intent.getStringExtra("EVENT_ID") ?: return
        val repository = FirebaseRepository()

        lifecycleScope.launch {
            val eventResult = repository.getEventById(eventId)

            if (eventResult.isSuccess) {
                val event = eventResult.getOrNull() ?: return@launch

                findViewById<TextView>(R.id.tvDetailTitle).text = event.title
                findViewById<TextView>(R.id.tvDetailDate).text = "Date: ${event.date}"
                findViewById<TextView>(R.id.tvDetailVenue).text = "Venue: ${event.venue}"
                findViewById<TextView>(R.id.tvDetailDescription).text = event.description
                findViewById<TextView>(R.id.tvDetailCapacity).text =
                    "Capacity: ${event.registeredCount}/${event.capacity}"
                findViewById<TextView>(R.id.tvDetailRewards).text =
                    "Reward: ${event.rewardPoints} points"

                val btnRegister = findViewById<Button>(R.id.btnDetailRegister)
                val userId = repository.getCurrentUser()?.uid ?: return@launch

                val isRegisteredResult = repository.isRegistered(eventId, userId)
                val isRegistered = isRegisteredResult.getOrNull() ?: false

                btnRegister.text = if (isRegistered) "Already Registered" else "Register Now"
                btnRegister.isEnabled = !isRegistered

                btnRegister.setOnClickListener {
                    lifecycleScope.launch {
                        val registerResult = repository.registerForEvent(eventId, userId)
                        if (registerResult.isSuccess) {
                            Toast.makeText(
                                this@EventDetailsActivity,
                                "Registration successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            btnRegister.text = "Already Registered"
                            btnRegister.isEnabled = false
                        } else {
                            Toast.makeText(
                                this@EventDetailsActivity,
                                registerResult.exceptionOrNull()?.message ?: "Registration failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@EventDetailsActivity, "Failed to load event", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}