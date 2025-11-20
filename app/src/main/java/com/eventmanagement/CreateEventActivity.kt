package com.eventmanagement

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eventmanagement.data.FirebaseRepository
import com.eventmanagement.models.Event
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.util.Calendar

class CreateEventActivity : AppCompatActivity() {

    private lateinit var etEventTitle: TextInputEditText
    private lateinit var etEventDescription: TextInputEditText
    private lateinit var etEventDate: TextInputEditText
    private lateinit var etEventVenue: TextInputEditText
    private lateinit var etEventCapacity: TextInputEditText
    private lateinit var etEventRewardPoints: TextInputEditText
    private lateinit var btnCreateEvent: Button
    private lateinit var btnCancel: Button

    private val repository = FirebaseRepository()
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Create Event"

        // Initialize views
        etEventTitle = findViewById(R.id.etEventTitle)
        etEventDescription = findViewById(R.id.etEventDescription)
        etEventDate = findViewById(R.id.etEventDate)
        etEventVenue = findViewById(R.id.etEventVenue)
        etEventCapacity = findViewById(R.id.etEventCapacity)
        etEventRewardPoints = findViewById(R.id.etEventRewardPoints)
        btnCreateEvent = findViewById(R.id.btnCreateEvent)
        btnCancel = findViewById(R.id.btnCancel)

        // Date picker
        etEventDate.setOnClickListener {
            showDatePicker()
        }

        btnCreateEvent.setOnClickListener {
            createEvent()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Format: YYYY-MM-DD
            selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            etEventDate.setText(selectedDate)
        }, year, month, day).show()
    }

    private fun createEvent() {
        val title = etEventTitle.text.toString().trim()
        val description = etEventDescription.text.toString().trim()
        val date = etEventDate.text.toString().trim()
        val venue = etEventVenue.text.toString().trim()
        val capacityStr = etEventCapacity.text.toString().trim()
        val rewardPointsStr = etEventRewardPoints.text.toString().trim()

        // Validation
        when {
            title.isEmpty() -> {
                etEventTitle.error = "Title is required"
                etEventTitle.requestFocus()
                return
            }
            description.isEmpty() -> {
                etEventDescription.error = "Description is required"
                etEventDescription.requestFocus()
                return
            }
            date.isEmpty() -> {
                etEventDate.error = "Date is required"
                etEventDate.requestFocus()
                return
            }
            venue.isEmpty() -> {
                etEventVenue.error = "Venue is required"
                etEventVenue.requestFocus()
                return
            }
            capacityStr.isEmpty() -> {
                etEventCapacity.error = "Capacity is required"
                etEventCapacity.requestFocus()
                return
            }
            rewardPointsStr.isEmpty() -> {
                etEventRewardPoints.error = "Reward points is required"
                etEventRewardPoints.requestFocus()
                return
            }
        }

        val capacity = capacityStr.toIntOrNull()
        val rewardPoints = rewardPointsStr.toIntOrNull()

        if (capacity == null || capacity <= 0) {
            etEventCapacity.error = "Invalid capacity"
            etEventCapacity.requestFocus()
            return
        }

        if (rewardPoints == null || rewardPoints < 0) {
            etEventRewardPoints.error = "Invalid reward points"
            etEventRewardPoints.requestFocus()
            return
        }

        // Create event
        btnCreateEvent.isEnabled = false
        btnCreateEvent.text = "Creating..."

        val event = Event(
            id = "",
            title = title,
            description = description,
            date = date,
            venue = venue,
            capacity = capacity,
            registeredCount = 0,
            rewardPoints = rewardPoints,
            imageUrl = ""
        )

        lifecycleScope.launch {
            val result = repository.createEvent(event)

            if (result.isSuccess) {
                Toast.makeText(
                    this@CreateEventActivity,
                    "Event created successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this@CreateEventActivity,
                    "Failed: ${result.exceptionOrNull()?.message}",
                    Toast.LENGTH_LONG
                ).show()
                btnCreateEvent.isEnabled = true
                btnCreateEvent.text = "Create Event"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}