package com.eventmanagement.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventmanagement.EventDetailsActivity
import com.eventmanagement.data.MockDataProvider
import com.eventmanagement.databinding.ItemEventBinding
import com.eventmanagement.models.Event

class EventsAdapter(
    private var events: List<Event>,
    private val onRegisterClick: (Event) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.tvTitle.text = event.title
            binding.tvDate.text = "Date: ${event.date}"
            binding.tvVenue.text = "Venue: ${event.venue}"
            binding.tvDescription.text = event.description
            binding.tvCapacity.text = "${event.registeredCount}/${event.capacity}"
            binding.tvRewardPoints.text = "⭐ ${event.rewardPoints} points"

            val isRegistered = MockDataProvider.isRegistered(event.id)
            binding.btnRegister.text = if (isRegistered) "Registered" else "Register"
            binding.btnRegister.isEnabled = !isRegistered

            binding.btnRegister.setOnClickListener {
                onRegisterClick(event)
            }

            // Add click listener to open event details
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, EventDetailsActivity::class.java)
                intent.putExtra("EVENT_ID", event.id)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size

    fun updateList(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}