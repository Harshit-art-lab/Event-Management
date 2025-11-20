package com.eventmanagement.fragments

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eventmanagement.R
import com.eventmanagement.adapters.EventsAdapter
import com.eventmanagement.data.MockDataProvider
import com.eventmanagement.models.Event

class EventsFragment : Fragment() {

    private lateinit var rvEvents: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var adapter: EventsAdapter
    private var allEvents = listOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        rvEvents = view.findViewById(R.id.rvEvents)
        etSearch = view.findViewById(R.id.etSearch)

        val repository = com.eventmanagement.data.FirebaseRepository()

        // Load events from Firebase
        lifecycleScope.launch {
            val result = repository.getAllEvents()
            if (result.isSuccess) {
                allEvents = result.getOrNull() ?: emptyList()

                adapter = EventsAdapter(allEvents) { event ->
                    // Handle registration
                    lifecycleScope.launch {
                        val userId = repository.getCurrentUser()?.uid ?: return@launch

                        val isRegisteredResult = repository.isRegistered(event.id, userId)
                        if (isRegisteredResult.getOrNull() == true) {
                            Toast.makeText(context, "Already registered", Toast.LENGTH_SHORT).show()
                        } else {
                            val registerResult = repository.registerForEvent(event.id, userId)
                            if (registerResult.isSuccess) {
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                                // Reload events to update count
                                val updatedEvents = repository.getAllEvents()
                                if (updatedEvents.isSuccess) {
                                    allEvents = updatedEvents.getOrNull() ?: emptyList()
                                    adapter.updateList(allEvents)
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    registerResult.exceptionOrNull()?.message ?: "Registration failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                rvEvents.layoutManager = LinearLayoutManager(context)
                rvEvents.adapter = adapter
            } else {
                Toast.makeText(context, "Failed to load events", Toast.LENGTH_SHORT).show()
            }
        }

        // Search functionality
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterEvents(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterEvents(query: String) {
        val filtered = if (query.isEmpty()) {
            allEvents
        } else {
            allEvents.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
        adapter.updateList(filtered)
    }
}
