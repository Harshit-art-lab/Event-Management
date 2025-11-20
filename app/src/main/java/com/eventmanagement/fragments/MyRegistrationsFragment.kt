package com.eventmanagement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eventmanagement.R
import com.eventmanagement.adapters.RegistrationsAdapter
import com.eventmanagement.data.FirebaseRepository
import kotlinx.coroutines.launch

class MyRegistrationsFragment : Fragment() {

    private lateinit var rvEvents: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var adapter: RegistrationsAdapter

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

        etSearch.visibility = View.GONE

        val repository = FirebaseRepository()

        // Load registrations from Firebase
        lifecycleScope.launch {
            val userId = repository.getCurrentUser()?.uid

            if (userId == null) {
                Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val registrationsResult = repository.getMyRegistrations(userId)

            if (registrationsResult.isSuccess) {
                val registrations = registrationsResult.getOrNull() ?: emptyList()

                // Get event details for each registration
                val events = registrations.mapNotNull { reg ->
                    val eventResult = repository.getEventById(reg.eventId)
                    if (eventResult.isSuccess) {
                        Pair(eventResult.getOrNull()!!, reg)
                    } else null
                }

                adapter = RegistrationsAdapter(events) { eventId ->
                    lifecycleScope.launch {
                        val unregisterResult = repository.unregisterFromEvent(eventId, userId)
                        if (unregisterResult.isSuccess) {
                            Toast.makeText(context, "Unregistered successfully", Toast.LENGTH_SHORT).show()
                            // Refresh list
                            onViewCreated(view, savedInstanceState)
                        } else {
                            Toast.makeText(
                                context,
                                unregisterResult.exceptionOrNull()?.message ?: "Failed to unregister",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                rvEvents.layoutManager = LinearLayoutManager(context)
                rvEvents.adapter = adapter

                // Show message if no registrations
                if (events.isEmpty()) {
                    Toast.makeText(context, "No registrations yet", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Failed to load registrations: ${registrationsResult.exceptionOrNull()?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}